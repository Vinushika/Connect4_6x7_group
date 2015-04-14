package c_minimax;

import java.util.Random;
import java.util.TreeMap;

//author: Gary Kalmanovich; rights reserved

public class Connect4StrategyB implements InterfaceStrategy {
    TreeMap<Long,Integer> checkedPositions = new TreeMap<Long,Integer>(); // minor slowdown @16.7 million (try mapDB?)
    Random rand = new Random(); // One can seed with a parameter variable here
    
    @Override
    public InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context) {
        return negamax(position, context, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }
    
    public InterfaceSearchResult negamax(InterfacePosition position, InterfaceSearchContext context, float alpha, float beta) {
    	InterfaceSearchResult searchResult = new Connect4SearchResult(); // Return information

        Integer checkedResult = checkedPositions.get(position.getRawPosition());
        if (checkedResult != null) {
            searchResult.setClassStateFromCompacted(checkedResult);
        } else { // position is not hashed, so let's see if we can process it
    
            int player   = position.getPlayer();
            int opponent = 3-player; // There are two players, 1 and 2.

            int  nRandom = rand.nextInt(position.nC());
            float uncertaintyPenalty = .01f;
            
            for ( int iC_raw = 0; iC_raw < position.nC(); iC_raw++) {
                int iC = (iC_raw+nRandom)% position.nC();
                InterfacePosition posNew = new Connect4Position( position );
                InterfaceIterator iPos   = new Connect4Iterator( position.nC(), position.nR() ); iPos.set(iC, 0);
                int iR = position.nR() - posNew.getChipCount(iPos) - 1;                          iPos.set(iC,iR); 
                if (iR >= 0) { // The column is not yet full
                    posNew.setColor(iPos, player);
                    int isWin = posNew.isWinner( iPos ); // iPos
                    float score;
                    if        ( isWin ==   player ) { score =  1f;  // Win
                    } else if ( isWin ==        0 ) { score =  0f;  // Draw
                    } else if ( isWin == opponent ) { score = -1f;  // Loss
                    } else { // Game is not over, so check further down the game
                        if ( context.getCurrentDepth()   < context.getMaxDepthSearchForThisPos() &&     // No more than max
                             context.getCurrentDepth()   < context.getMinDepthSearchForThisPos()    ) { // No more than min
                            posNew.setPlayer(opponent);
                            context.setCurrentDepth(context.getCurrentDepth()+1);
                            InterfaceSearchResult opponentResult = negamax(posNew,context,-beta,-alpha); // Return information is in opponentContext
                            context.setCurrentDepth(context.getCurrentDepth()-1);
                            score = -opponentResult.getBestScoreSoFar();
                            // Note, for player, opponent's best move has negative worth
                            //   That is because, score = ((probability of win) - (probability of loss))
                            
                            if ( opponentResult.isResultFinal() == false ) { // if the result is not final, reverse penalty
                                searchResult.setIsResultFinal(false);
                                score -= 2*uncertaintyPenalty;
                            }
                        } else { 
                            // We cannot recurse further down the minimax search
                            // We cannot recurse further down the minimax search
                        	//play n random boards, collect score
                        	int numWin = 0;
                        	int numLose = 0;
                        	int numDraws = 0;
                        	float total_plays = 10.0f; //change this if we ever want to play less or more
                        	for (int i = 0; i < total_plays; i++) {
                        		int winner = playRandomlyUntilEnd(posNew,player);
                        		//ok, we have an end state.
                        		if (winner == player) {
                        			//we win!
                        			numWin++;
                        		} else if (winner == opponent) {
                        			//we lose!
                        			numLose++;
                        		}else {
                        			numDraws++;
                        		}
                        	}
                            score = (numWin - numLose - numDraws) / total_plays;
//                            score = -uncertaintyPenalty;
                            searchResult.setIsResultFinal(false);
                        }
                    }
    
                    if (searchResult.getBestScoreSoFar() <  score ) {
                        searchResult.setBestMoveSoFar(iPos, score );
                        if ( score == 1f ) break; // No need to search further if one can definitely win
                    }
                    alpha = Math.max(alpha, score);
                    if (alpha >= beta) {
                    	break; // alpha beta pruning
                    }
                }
                long timeNow = System.nanoTime();
                if ( context.getMaxSearchTimeForThisPos() - timeNow <= 0 ) {
                    System.out.println("Connect4StrategyB:getBestMove(): ran out of time: maxTime("
                            +context.getMaxSearchTimeForThisPos()+") :time("
                            +timeNow+"): recDepth("+context.getCurrentDepth()+")");
                    break; // Need to make any move now
                }
            }
    
            //if (searchResult.isResultFinal() && position.getChipCount()%3==1) // Hash this result
            //    checkedPositions.put(position.getRawPosition(),searchResult.getClassStateCompacted());

        }
        
        return searchResult;
    }

    public int playRandomlyUntilEnd(InterfacePosition pos, int player) {
    	//strategy for this code: while the position is not an ending position,
    	//keep making random moves until someone wins, then return the score
    	//the calling code calls this 100 times, and computes how many times are win
    	//vs how many times are loss, over 100
    	//draws are taken out of the equation
    	//this should never be called starting from a position with no fillable spots
    	int current_player = 3 - player;
        InterfacePosition posNew = new Connect4Position( pos);
    	while (posNew.isWinner() == -1) {
    		//find a position that is playable by iterating through the columns
    		boolean isFillable = false;
    		int final_iC = -1;
    		int final_iR = -1;
            InterfaceIterator iPos   = new Connect4Iterator( posNew.nC(), posNew.nR() );
    		while (!isFillable) {
                int  nRandom = rand.nextInt(posNew.nC()); //generate random integer for column
                iPos.set(nRandom, 0); //check the first row associated to the column
                int iR = posNew.nR() - posNew.getChipCount(iPos) - 1; //see if the column isn't full           
                iPos.set(nRandom,iR);
                if (iR >= 0) {
                	//it's fillable, so let's put something in it
                	isFillable = true;
                	final_iR = iR;
                	final_iC = nRandom;
                	break; //defensive programming
                }
    		}
    		//we have a playable position, let's play it
    		posNew.setPlayer(current_player);
    		iPos.set(final_iC,final_iR);
    		posNew.setColor(iPos, current_player);
    		current_player = 3 - current_player;
    	}
    	return posNew.isWinner();
    }
    
    @Override
    public void setContext(InterfaceSearchContext strategyContext) {
        // Not used in this strategy
    }

    @Override
    public InterfaceSearchContext getContext() {
        // Not used in this strategy
        return null;
    }
}

package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class TicTacToeStrategy implements InterfaceStrategy {
    @Override
    public InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context) {
        InterfaceSearchResult searchResult = new TicTacToeSearchResult(); // Return information

        int player = position.getPlayer();
        int opponent = 3-player; // There are two players, 1 and 2.
        for ( InterfaceIterator iPos = new TicTacToeIterator(); iPos.isInBounds(); iPos.increment() ) {
            InterfacePosition posNew = new TicTacToePosition(position);
            if (posNew.getColor(iPos) == 0) { // This is a free spot
                posNew.setColor(iPos, player);
                int isWin = posNew.isWinner();
                float score;
                if        ( isWin ==   player ) { score =  1f;  // Win
                } else if ( isWin ==        0 ) { score =  0f;  // Draw
                } else if ( isWin == opponent ) { score = -1f;  // Loss
                } else { // Game is not over, so check further down the game
                    posNew.setPlayer(opponent);
                    InterfaceSearchResult opponentResult = getBestMove(posNew, context); // Return information is in opponentContext
                    score = -opponentResult.getBestScoreSoFar();
                    // Note, for player, opponent's best move has negative worth
                    //   That is because, score = ((probability of win) - (probability of loss))
                }

                if (searchResult.getBestScoreSoFar() <  score ) {
                    searchResult.setBestMoveSoFar(iPos, score );
                }
            }
        }
        
        return searchResult;
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

package c_minimax;

import java.io.File;
import java.util.TreeMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

//author: Gary Kalmanovich; rights reserved

public class Connect4StrategyB implements InterfaceStrategy {
	TreeMap<Long, Integer> checkedPositions = new TreeMap<Long, Integer>(); // minor
																			// slowdown
																			// @16.7
																			// million
																			// (try
																			// mapDB?)

	static DB db = DBMaker.fileDB(new File("scores.db")) // or memory db
			.cacheSize(1000000) // optionally change cache size
			.make();
	static HTreeMap<Long, Integer> map = db.createHashMap("map").keySerializer(Serializer.LONG).valueSerializer(Serializer.INTEGER).makeOrGet();

	FastRandomizer rand = new FastRandomizer(); // One can seed with a parameter
												// variable here
	int[] probability_distribution = new int[] { 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6 };

	// 1/16 for 0
	// 1/8 for 1
	// 3/16 for 2
	// 1/4 for 3 (center)
	// 3/16 for 4
	// 1/8 for 5
	// 1/16 for 6
	// this is a center-based distribution, adds up to 1, we can use it to get
	// better random games.

	@Override
	public InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context) {
		return negamax(position, context, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	public InterfaceSearchResult negamax(InterfacePosition position, InterfaceSearchContext context, float alpha, float beta) {
		InterfaceSearchResult searchResult = new Connect4SearchResult(); // Return
																			// information

		Integer checkedResult = checkedPositions.get(position.getRawPosition());
		if (checkedResult != null) {
			searchResult.setClassStateFromCompacted(checkedResult);
		} else { // position is not hashed, so let's see if we can process it

			int player = position.getPlayer();
			int opponent = 3 - player; // There are two players, 1 and 2.

			int randomIndex = rand.nextInt(32);
			int nRandom = probability_distribution[randomIndex];
			float uncertaintyPenalty = .01f;

			for (int iC_raw = 0; iC_raw < position.nC(); iC_raw++) {
				int iC = (iC_raw + nRandom) % position.nC();
				InterfacePosition posNew = new Connect4Position(position);
				InterfaceIterator iPos = new Connect4Iterator(position.nC(), position.nR());
				iPos.set(iC, 0);
				int iR = position.nR() - posNew.getChipCount(iPos) - 1;
				iPos.set(iC, iR);
				if (iR >= 0) { // The column is not yet full
					if (searchResult.getBestMoveSoFar() == null)
						searchResult.setBestMoveSoFar(iPos, searchResult.getBestScoreSoFar());
					posNew.setColor(iPos, player);
					int isWin = posNew.isWinner(iPos); // iPos
					float score;
					if (isWin == player) {
						score = 1f; // Win
					} else if (isWin == 0) {
						score = 0f; // Draw
					} else if (isWin == opponent) {
						score = -1f; // Loss
					} else { // Game is not over, so check further down the game
						Integer s = map.get(posNew.getRawPosition());
						if (s != null) {
							score = s;
						} else {
							if (context.getCurrentDepth() < context.getMaxDepthSearchForThisPos() && context.getCurrentDepth() < context.getMinDepthSearchForThisPos()) {
								posNew.setPlayer(opponent);
								context.setCurrentDepth(context.getCurrentDepth() + 1);
								InterfaceSearchResult opponentResult = negamax(posNew, context, -beta, -alpha);
								context.setCurrentDepth(context.getCurrentDepth() - 1);
								score = -opponentResult.getBestScoreSoFar();
								synchronized (map) {
									map.put(posNew.getRawPosition(), (int) score);
								}
								// Note, for player, opponent's best move has
								// negative worth
								// That is because, score = ((probability of
								// win) -
								// (probability of loss))

								if (opponentResult.isResultFinal() == false) { // if
																				// the
																				// result
																				// is
																				// not
																				// final,
																				// reverse
																				// penalty
									searchResult.setIsResultFinal(false);
									score -= 2 * uncertaintyPenalty;
								}
							} else {
								// We cannot recurse further down the minimax
								// search
								// We cannot recurse further down the minimax
								// search
								// play n random boards, collect score
								int numWin = 0;
								int numLose = 0;
								int numDraws = 0;
								float total_plays = 30.0f; // change this if we
															// ever
															// want to play less
															// or
															// more
								for (int i = 0; i < total_plays; i++) {
									int winner = playRandomlyUntilEnd(posNew, player);
									// ok, we have an end state.
									if (winner == player) {
										// we win!
										numWin++;
									} else if (winner == opponent) {
										// we lose!
										numLose++;
									} else {
										numDraws++;
									}
								}
								score = (numWin - numLose) / total_plays;
								synchronized (map) {
									map.put(posNew.getRawPosition(), (int) score);
								}
								// score = -uncertaintyPenalty;
								searchResult.setIsResultFinal(false);
							}
						}
					}

					if (searchResult.getBestMoveSoFar() == null || searchResult.getBestScoreSoFar() < score) {
						searchResult.setBestMoveSoFar(iPos, score);
						if (score == 1f)
							break; // No need to search further if one can
									// definitely win
					}
					alpha = Math.max(alpha, score);
					if (alpha >= beta) {
						break; // alpha beta pruning
					}
				}
				long timeNow = System.nanoTime();
				if (context.getMaxSearchTimeForThisPos() - timeNow <= 0) {
					System.out.println("Connect4Strategy:getBestMove(): ran out of time: maxTime(" + context.getMaxSearchTimeForThisPos() + ") :time(" + timeNow + "): recDepth("
							+ context.getCurrentDepth() + ")");
					if (context.getCurrentDepth() == 0) {
						// Revert back to a lesser search
						System.out.print("Connect4Strategy: Depth limit of " + context.getMinDepthSearchForThisPos() + " -> ");
						context.setMinDepthSearchForThisPos(context.getMinDepthSearchForThisPos() - 1);
						System.out.println(context.getMinDepthSearchForThisPos());
					}
					if (((Connect4SearchContext) context).getOriginalPlayer() == opponent) { // TODO:
																								// add
																								// to
																								// interface
						searchResult.setBestMoveSoFar(searchResult.getBestMoveSoFar(), 0.95f); // Set
																								// to
																								// original
																								// opponent
																								// almost-win
					}
					searchResult.setIsResultFinal(false);
					break; // Need to make any move now
				}
			}

			// if (searchResult.isResultFinal() && position.getChipCount()%3==1)
			// // Hash this result
			// checkedPositions.put(position.getRawPosition(),searchResult.getClassStateCompacted());

		}

		// if we haven't run out of time yet, then increase the depth
		long timeLeftInNanoSeconds = context.getMaxSearchTimeForThisPos() - System.nanoTime();
		if (context.getCurrentDepth() == 0 && !searchResult.isResultFinal() && timeLeftInNanoSeconds > ((Connect4SearchContext) context).getOriginalTimeLimit() * 9 / 10) { // TODO:
																																											// add
																																											// to
																																											// interface
			System.out.print("Connect4StrategyB: Depth limit of " + context.getMinDepthSearchForThisPos() + " -> ");
			context.setMinDepthSearchForThisPos(context.getMinDepthSearchForThisPos() + 1);
			System.out.println(context.getMinDepthSearchForThisPos());
			InterfaceSearchResult anotherResult = negamax(position, context, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
			if (anotherResult.getBestScoreSoFar() > searchResult.getBestScoreSoFar()) {
				searchResult.setBestMoveSoFar(anotherResult.getBestMoveSoFar(), anotherResult.getBestScoreSoFar());
				searchResult.setIsResultFinal(anotherResult.isResultFinal());
			}
		}
		return searchResult;
	}

	public int playRandomlyUntilEnd(InterfacePosition pos, int player) {
		// strategy for this code: while the position is not an ending position,
		// keep making random moves until someone wins, then return the score
		// the calling code calls this 100 times, and computes how many times
		// are win
		// vs how many times are loss, over 100
		// draws are taken out of the equation
		// this should never be called starting from a position with no fillable
		// spots
		int current_player = 3 - player;
		InterfacePosition posNew = new Connect4Position(pos);
		while (posNew.isWinner() == -1) {
			// find a position that is playable by iterating through the columns
			boolean isFillable = false;
			int final_iC = -1;
			int final_iR = -1;
			InterfaceIterator iPos = new Connect4Iterator(posNew.nC(), posNew.nR());
			while (!isFillable) {
				int randomIndex = rand.nextInt(32); // generate random integer
													// for column
				int nRandom = probability_distribution[randomIndex];
				iPos.set(nRandom, 0); // check the first row associated to the
										// column
				int iR = posNew.nR() - posNew.getChipCount(iPos) - 1; // see if
																		// the
																		// column
																		// isn't
																		// full
				iPos.set(nRandom, iR);
				if (iR >= 0) {
					// it's fillable, so let's put something in it
					isFillable = true;
					final_iR = iR;
					final_iC = nRandom;
					break; // defensive programming
				}
			}
			// we have a playable position, let's play it
			posNew.setPlayer(current_player);
			iPos.set(final_iC, final_iR);
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

	public class FastRandomizer {
		long seed = System.nanoTime(); // spawned at launch

		/**
		 * Gets a number in the range (0,max_exclusive), exclusive
		 * 
		 * @param max_exclusive
		 * @return
		 */
		public int nextInt(int max_exclusive) {
			seed ^= (seed << 21);
			seed ^= (seed >>> 35);
			seed ^= (seed << 4);
			// use Math.abs because Java is dumb and doesn't do unsigned longs
			return (int) Math.abs(seed % max_exclusive);
		}
	}
}

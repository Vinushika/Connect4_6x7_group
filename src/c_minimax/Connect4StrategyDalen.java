package c_minimax;

import java.util.Random;
import java.util.TreeMap;

//author: Gary Kalmanovich; rights reserved

public class Connect4StrategyDalen implements InterfaceStrategy {

	// Bonus Point Values
	private final static float RED = .0006f; // 0f;
	private final static float ORN = .0005f; // -.0001f;
	private final static float YEL = .0004f; // -.0002f;
	private final static float GRN = .0003f; // -.0003f;
	private final static float BLU = .0002f; // -.0004f;
	private final static float IND = .0001f; // -.0005f;
	private final static float VLT = 0f; // -.0006f;

	// Bonus Point Value of each Position
	private final static float[][] bonusMap = { { VLT, IND, GRN, YEL, GRN, IND, VLT }, // Row
																						// 0
			{ VLT, GRN, YEL, ORN, YEL, GRN, VLT }, // Row 1
			{ IND, GRN, ORN, RED, ORN, GRN, IND }, // Row 2
			{ IND, GRN, ORN, RED, ORN, GRN, IND }, // Row 3
			{ VLT, GRN, YEL, ORN, YEL, GRN, VLT }, // Row 4
			{ VLT, BLU, GRN, YEL, GRN, BLU, VLT }, // Row 5
	};

	TreeMap<Long, Integer> checkedPositions = new TreeMap<Long, Integer>(); // minor
																			// slowdown
																			// @16.7
																			// million
																			// (try
																			// mapDB?)
	Random rand = new Random(); // One can seed with a parameter variable here

	Connect4StrategyDalen() {
		int seed = rand.nextInt();
		System.out.println("Connect4StrategyB: Seed set to: " + seed);
		rand.setSeed(seed);
	}

	@Override
	public InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context) {
		InterfaceSearchResult searchResult = new Connect4SearchResult(); // Return
																			// information

		Integer checkedResult = checkedPositions.get(position.getRawPosition());
		if (checkedResult != null) {
			searchResult.setClassStateFromCompacted(checkedResult);
		} else { // position is not hashed, so let's see if we can process it

			int player = position.getPlayer();
			int opponent = 3 - player; // There are two players, 1 and 2.

			int nRandom = rand.nextInt(position.nC());
			float uncertaintyPenalty = .01f;

			for (int iC_raw = 0; iC_raw < position.nC(); iC_raw++) {
				int iC = (iC_raw + nRandom) % position.nC();
				InterfacePosition posNew = new Connect4Position(position);
				InterfaceIterator iPos = new Connect4Iterator(position.nC(), position.nR());
				iPos.set(iC, 0);
				int iR = position.nR() - posNew.getChipCount(iPos) - 1;
				iPos.set(iC, iR);
				if (iR >= 0) { // The column is not yet full
					// Check if we have any legal move yet, if no, add this so
					// we have a !=null move
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

						score = 0;
						score += Connect4StrategyDalen.getPositionBonus(iPos);
						score += Connect4StrategyDalen.getThreatBonus(iPos);

						if (context.getCurrentDepth() < context.getMaxDepthSearchForThisPos() && // No
																									// more
																									// than
																									// max
								context.getCurrentDepth() < context.getMinDepthSearchForThisPos()) { // No
																										// more
																										// than
																										// min
							posNew.setPlayer(opponent);
							context.setCurrentDepth(context.getCurrentDepth() + 1);
							InterfaceSearchResult opponentResult = getBestMove(posNew, context); // Return
																									// information
																									// is
																									// in
																									// opponentContext
							context.setCurrentDepth(context.getCurrentDepth() - 1);

							score -= opponentResult.getBestScoreSoFar(); // Changed
																			// from
																			// score
																			// =
																			// -opponentResult

							// Note, for player, opponent's best move has
							// negative worth
							// That is because, score = ((probability of win) -
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
							// We cannot recurse further down the minimax search

							// Here I am calling valuePosition in
							// Connect4Position because it is static.
							// However, if your position valuation is based on
							// Monte-Carlo game playing,
							// valuePosition is likely in this class.
							score += (player == 1 ? 1 : -1) * posNew.valuePosition() - uncertaintyPenalty;
							searchResult.setIsResultFinal(false);
						}
					}

					if (searchResult.getBestMoveSoFar() == null || searchResult.getBestScoreSoFar() < score) {
						searchResult.setBestMoveSoFar(iPos, score);
						if (score == 1f)
							break; // No need to search further if one can
									// definitely win
					}
				}
				long timeNow = System.nanoTime();
				if (context.getMaxSearchTimeForThisPos() - timeNow <= 0) {
					System.out.println("Connect4StrategyB:getBestMove(): ran out of time: maxTime(" + context.getMaxSearchTimeForThisPos() + ") :time(" + timeNow + "): recDepth("
							+ context.getCurrentDepth() + ")");
					if (context.getCurrentDepth() == 0) {
						// Revert back to a lesser search
						System.out.print("Connect4StrategyB: Depth limit of " + context.getMinDepthSearchForThisPos() + " -> ");
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
																								// win
					}
					searchResult.setIsResultFinal(false);
					break; // Need to make any move now
				}
			}

			// if (searchResult.isResultFinal() && position.getChipCount()%3==1)
			// // Hash this result
			// checkedPositions.put(position.getRawPosition(),searchResult.getClassStateCompacted());

		}

		long timeLeftInNanoSeconds = context.getMaxSearchTimeForThisPos() - System.nanoTime();
		if (context.getCurrentDepth() == 0 && !searchResult.isResultFinal() && timeLeftInNanoSeconds > ((Connect4SearchContext) context).getOriginalTimeLimit() * 9 / 10) { // TODO:
																																											// add
																																											// to
																																											// interface
			System.out.print("Connect4StrategyB: Depth limit of " + context.getMinDepthSearchForThisPos() + " -> ");
			context.setMinDepthSearchForThisPos(context.getMinDepthSearchForThisPos() + 1);
			System.out.println(context.getMinDepthSearchForThisPos());
			InterfaceSearchResult anotherResult = getBestMove(position, context);
			if (anotherResult.getBestScoreSoFar() > searchResult.getBestScoreSoFar()) {
				searchResult.setBestMoveSoFar(anotherResult.getBestMoveSoFar(), anotherResult.getBestScoreSoFar());
				searchResult.setIsResultFinal(anotherResult.isResultFinal());
			}
		}

		return searchResult;
	}

	private static float getPositionBonus(InterfaceIterator iPos) {
		return bonusMap[iPos.iR()][iPos.iC()];
	}

	private static float getThreatBonus(InterfaceIterator iPos) {
		int nR = iPos.nR();
		int nC = iPos.nC();
		return 0;
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

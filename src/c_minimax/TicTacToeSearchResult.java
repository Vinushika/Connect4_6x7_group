package c_minimax;

//author: Gary Kalmanovich; rights reserved

class TicTacToeSearchResult implements InterfaceSearchResult {

    InterfaceIterator bestMoveSoFar  = null;
    float             bestScoreSoFar = Float.NEGATIVE_INFINITY;

    @Override
    public InterfaceIterator getBestMoveSoFar() {
        return bestMoveSoFar;
    }

    @Override
    public float getBestScoreSoFar() {
        return bestScoreSoFar;
    }

    @Override
    public void setBestMoveSoFar(InterfaceIterator newMove, float newScore) {
        bestMoveSoFar  = new TicTacToeIterator(newMove);
        bestScoreSoFar = newScore;
    }

    @Override
    public float getOpponentBestScoreOnPreviousMoveSoFar() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setOpponentBestScoreOnPreviousMoveSoFar(float scoreToBeat) {
        // Not used in this strategy
    }

    @Override
    public int getClassStateCompacted() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setClassStateFromCompacted(int compacted) {
        // Not used in this strategy
    }

    @Override
    public void setIsResultFinal(boolean isFinal) {
        // Not used in this strategy
    }

    @Override
    public boolean isResultFinal() {
        // Not used in this strategy
        return false;
    }

}

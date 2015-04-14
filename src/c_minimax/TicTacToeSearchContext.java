package c_minimax;

//author: Gary Kalmanovich; rights reserved

class TicTacToeSearchContext implements InterfaceSearchContext {

    @Override
    public int getMinDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMinDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public int getMaxDepthSearchForThisPos() {
        // Not used in this strategy
        return 0;
    }

    @Override
    public void setMaxDepthSearchForThisPos(int minDepth) {
        // Not used in this strategy
    }

    @Override
    public long getMaxSearchTimeForThisPos() {
        // Not used in this strategy
        return 0/0;
    }

    @Override
    public void setMaxSearchTimeForThisPos(long maxTime) {
        // Not used in this strategy
    }

    @Override
    public int getCurrentDepth() {
        // Not used in this strategy
        return 0/0;
    }

    @Override
    public void setCurrentDepth(int minDepth) {
        // Not used in this strategy
    }

}

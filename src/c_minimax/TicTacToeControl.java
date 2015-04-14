package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class TicTacToeControl implements InterfaceControl{
    // This is the controller class. 
    // In general this class would also be a model class.
    //   However, I used code from someone else that keeps a different architecture
    // Its responsibilities would be:
    //   - to make sure that the rules of the game are followed
    //   - to check if there is a winner in a particular position
    //   - to notify Strategy if Strategy is playing
    //   - this class can keep current state of the game
    // As is, most of the relevant responsibility is with the GameManager and Game classes
    // These are available to this class through composition
    
    private InterfaceView     view;
    private GameManager       gameManager;
    private InterfaceStrategy xStrategy = null;// new TicTacToeStrategy();// 
    private InterfaceStrategy oStrategy = null;// new TicTacToeStrategy();// 
    
    @Override
    public void setView(InterfaceView view) {
        if ( view instanceof TicTacToeView ) {
            this.view = view;
            gameManager = ((TicTacToeView) view).getGameManager();
        } else 
            System.err.println("Error: TicTacToeControl cannot accept this type of InterfaceView");
    }

    @Override 
    public InterfaceView getView() { return view; }

    private Game getGame() { return gameManager.getGame(); }

    @Override 
    public void setStrategy( int player, int strategy ) {
        InterfaceStrategy playerStrategy = strategy==0 ? null : new TicTacToeStrategy();
        if (player==1) xStrategy = playerStrategy;
        else           oStrategy = playerStrategy;
        onMove(); // Check if anything needs to be done via a strategy
    }
    
    @Override 
    public int getStrategy( int player ) {
        if (player==1) return xStrategy==null ? 0 : 1;
        else           return oStrategy==null ? 0 : 1;
    }

    @Override
    public void onMove() { // Control is notified of a player (real or automated) move
        InterfacePosition position = computePosition();
        long startTime = System.nanoTime(); // Start the total timing
        if (getGame().getCurrentPlayer() == Square.State.CROSS) {
            if (xStrategy != null) {
                position.setPlayer(1);
                InterfaceSearchContext context = new TicTacToeSearchContext();
                InterfaceSearchResult result = xStrategy.getBestMove(position, context);
                int iR = result.getBestMoveSoFar().iR();
                int iC = result.getBestMoveSoFar().iC();
                
                getGame().getBoard().getSquare(iC,iR).pressed();
            }
        }
        if (getGame().getCurrentPlayer() == Square.State.NOUGHT) {
            if (oStrategy != null) {
                position.setPlayer(2);
                InterfaceSearchContext context = new TicTacToeSearchContext();
                InterfaceSearchResult result = oStrategy.getBestMove(position, context);
                int iR = result.getBestMoveSoFar().iR();
                int iC = result.getBestMoveSoFar().iC();
                
                getGame().getBoard().getSquare(iC,iR).pressed();
            }
        }
        long endTime  = System.nanoTime(); // Finish the total timing
        System.out.println("This move computation took:   " + ((double)(endTime - startTime)/1000000.0) + " milliseconds");
    }

    @Override
    public void onMove( int i0C, int i0R, int i1C, int i1R, int iPlayer ) { // Specific notification of a move event (would invoke onMove())
        System.err.println("Warning: onMove(int,int,int,int,int) is not supported by TicTacToeControl");
        onMove();
    }

    @Override
    public void resetGame() {
        System.err.println("Warning: resetGame() is not supported by TicTacToeControl");
        onMove();
    }

    private InterfacePosition computePosition() {
        // Compute position
        InterfacePosition position = new TicTacToePosition();
        for (    int iR=0; iR<3; iR++) {
            for (int iC=0; iC<3; iC++) {
                Square.State state = getGame().getBoard().getSquare(iC, iR).getState();
                // Set color: 0 if empty, 1 if x(cross), 2 if o(nought)
                int color = (state == Square.State.EMPTY) ? 0 : (state == Square.State.CROSS) ? 1 : 2;
                InterfaceIterator iter = new TicTacToeIterator(); iter.set(iC, iR);
                position.setColor( iter, color);
            }
        }
        return position;
    }

    @Override
    public boolean isBlockManualMove() {
        System.err.println("Error: TicTacToeControl::isBlockManualMove is not implemented!!!");
        return false;
    }
}

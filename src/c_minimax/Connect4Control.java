package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class Connect4Control implements InterfaceControl, ThreadCompleteListener{
    // This is the controller class. It is also a model class
    // Its responsibilities are:
    //   - to make sure that the rules of the game are followed
    //   - to check if there is a winner in a particular position
    //   - to notify Strategy if Strategy is playing
    //   - this class can keep current state of the game
    //   

    private InterfaceView     view;
    private Connect4Position position; 
    private int currentPlayer = 1;
    private InterfaceStrategy player1Strategy = null;// new Connect4Strategy();// 
    private InterfaceStrategy player2Strategy = null;// new Connect4Strategy();// 
    private boolean isMoveBlockedByCalculation = false;
    private int preferedMinDepthPlayer1 = 5; // set to 5 again in resetGame() 
    private int preferedMinDepthPlayer2 = 5; // set to 5 again in resetGame()

    @Override
    public void onMove() { // Control is notified of a player (real or automated) move
        position.setPlayer( currentPlayer );
        Connect4SearchContext context = new Connect4SearchContext();
        context.setOriginalPlayer(currentPlayer);
        context.setMaxSearchTimeForThisPos(2000000000L);// 2 seconds (in nanoseconds) from now
        context.setMaxDepthSearchForThisPos( 15 ); // Search no more than ## moves deep
        if ( currentPlayer == 1 ) {
            if (player1Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
                context.setMinDepthSearchForThisPos(  preferedMinDepthPlayer1 ); // Search no less than ## moves deep
                isMoveBlockedByCalculation = true;
                NotifyingThread thread = new ThreadStrategy(player1Strategy, position, context);
                thread.addListener(this); // add ourselves as a listener
                thread.start();           // Start the Thread
            }
        }
        if ( currentPlayer == 2 ) {
            if (player2Strategy != null && !isBlockManualMove()) { // isStrategy and game not over
                context.setMinDepthSearchForThisPos(  preferedMinDepthPlayer2 ); // Search no less than ## moves deep
                isMoveBlockedByCalculation = true;
                NotifyingThread thread = new ThreadStrategy(player2Strategy, position, context);
                thread.addListener(this); // add ourselves as a listener
                thread.start();           // Start the Thread
            }
        }
    }

    @Override
    public void onMove(int unused1, int unused2, int iColumn, int iRow, int iColor) {
        InterfaceIterator iter = new Connect4Iterator( view.nC(), view.nR()); iter.set(iColumn, iRow);
        //System.out.print("Slot: ( "+iRow+", "+iColumn+" ) is set to color: "+iColor+". Was set to: "+position.getColor(iter));
        position.setColor(iter, iColor);
        //System.out.println(". Now: "+position.getColor(iter));
        currentPlayer = 3 - iColor;
        onMove();
    }

    @Override
    public void resetGame() {
        position.reset();
        currentPlayer = 1;
        preferedMinDepthPlayer1 = 5; // set to what was in constructor
        preferedMinDepthPlayer2 = 5; // set to what was in constructor
    }

    @Override
    public void setView(InterfaceView connect4View) {
        view = connect4View;
        position = new Connect4Position( view.nC(), view.nR() );
    }

    @Override
    public InterfaceView getView() {
        return view;
    }

    @Override 
    public void setStrategy( int player, int strategy ) {
        InterfaceStrategy playerStrategy = strategy==0 ? null : 
            strategy==1 ? new Connect4Strategy() : new Connect4StrategyB();
        if (player==1) player1Strategy = playerStrategy;
        else           player2Strategy = playerStrategy;
        onMove(); // Check if anything needs to be done via a strategy
    }
    
    @Override 
    public int getStrategy( int player ) {
        if (player==1) return player1Strategy==null ? 0 : 1;
        else           return player2Strategy==null ? 0 : 1;
    }
    
    @Override 
    public boolean isBlockManualMove() {
        return position.isWinner() >= 0  ||  isMoveBlockedByCalculation;
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        InterfaceSearchResult result   = ((ThreadStrategy)thread).getResult();
        //System.out.println("Best move(Applct): c "+bestMove.iC()+", r "+bestMove.iR());
        int iR = result.getBestMoveSoFar().iR();
        int iC = result.getBestMoveSoFar().iC();
        
        thread.interrupt();
        
        view.performMove(0, 0, iC, iR, 0); // 0 references is not used in this view

        InterfaceSearchContext context = ((ThreadStrategy)thread).getContext();
        if ( ((Connect4SearchContext)context).getOriginalPlayer() == 1 ) { // TODO: fix Connect4SearchContext cast
            preferedMinDepthPlayer1 = context.getMinDepthSearchForThisPos();
        } else {
            preferedMinDepthPlayer2 = context.getMinDepthSearchForThisPos();
        }

        isMoveBlockedByCalculation = false;
    }
}

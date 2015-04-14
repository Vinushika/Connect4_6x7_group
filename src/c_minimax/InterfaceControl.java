package c_minimax;

//author: Gary Kalmanovich; rights reserved

public interface InterfaceControl {
    void onMove(); // General notification of a move event to invoke strategy
    void onMove( int i0C, int i0R, int i1C, int i1R, int iPlayer ); // Specific notification of a move event (would invoke onMove())
    void resetGame(); // Start over the game -> reset any necessary instance variables
    void setView( InterfaceView view );
    InterfaceView getView();
    void setStrategy( int player, int strategy );
    int getStrategy( int player );
    boolean isBlockManualMove();
}

package c_minimax;

//author: Gary Kalmanovich; rights reserved

public interface InterfacePosition {
    public long getRawPosition();
    public int  getColor( InterfaceIterator iPos ); // get color at a spot
    public void setColor( InterfaceIterator iPos, int color ); // set color at a spot
    public void setPlayer( int iPlayer ); // set the player whose move it is
    public int  getPlayer(); // get the player whose move it is
    public int  getChipCount(); // get the number of pieces on the board
    public int  getChipCount( InterfaceIterator iPos ); // any local count, e.g., Connect4 column chip #
    public int  isWinner( InterfaceIterator iPos ); // Check only the move
    public int  isWinner(); // Check the entire position
    public float valuePosition(); // For example, (probability of winning)-(probability of losing)
    public void reset();
    public int  nC();
    public int  nR();
}

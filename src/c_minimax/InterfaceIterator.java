package c_minimax;

//author: Gary Kalmanovich; rights reserved

//This is an iterator for positions. E.g., over squares on a board-game
public interface InterfaceIterator { 
    public int     iC();
    public int     iR();
    public int     nC();
    public int     nR();
    public void    set( int iC, int iR );
    public void    set( InterfaceIterator iter );
    public void    increment();
    public void    resetBack();
    public boolean isInBounds();
}

package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class CheckersIterator implements InterfaceIterator {
    
    // NOTE, this iterator just iterates the black squares
    // One would need to write a different iterator (possibly, 
    // but not necessarily based on this one) to iterate over 
    // possible moves

    private int   iterator  = 1; // Starts at 1 and skips every other (+=2)
    private int nC;
    private int nR;
    
    CheckersIterator(int nC, int nR) { this.nC = nC; this.nR = nR; resetBack();} 
    
    @Override public int          iC() { return iterator%nC; }
    @Override public int          iR() { return iterator/nC; }
    @Override public int          nC() { return          nC; }
    @Override public int          nR() { return          nR; }
    @Override public void  increment() {       iterator +=2; }
    @Override public void  resetBack() {       iterator = 1; }
    @Override public void set( InterfaceIterator iter ) { iterator = ((CheckersIterator)iter).iterator; }
    @Override public void set(int iC, int iR) { assert (iR+iC)%2==1; iterator = nC*iR+iC; }
    @Override public boolean isInBounds() { return 0<=iterator && iterator<nC*nR; }

}

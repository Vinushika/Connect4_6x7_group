package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class Connect4Iterator implements InterfaceIterator {
    // This implementation assumes nC x nR //7 columns by 6 rows

    private int iterator  = 0;
    private int nC;
    private int nR;
    
    Connect4Iterator(int nC, int nR) { this.nC = nC; this.nR = nR; } 
    Connect4Iterator(InterfaceIterator iter) { this.nC = iter.nC(); this.nR = iter.nR(); this.set(iter); } 
    
    @Override public int          iC() { return iterator%nC; }//7
    @Override public int          iR() { return iterator/nC; }//7
    @Override public int          nC() { return          nC; }//7
    @Override public int          nR() { return          nR; }//6
    @Override public void  increment() {       iterator++  ; }
    @Override public void  resetBack() {       iterator = 0; }
    @Override public void set( InterfaceIterator iter ) { iterator = ((Connect4Iterator)iter).iterator; }
    @Override public void set(int iC, int iR) { iterator = nC*iR+iC; }//7
    @Override public boolean isInBounds() { return 0<=iterator && iterator<nC*nR; }//7*6

}

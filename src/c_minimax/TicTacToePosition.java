package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class TicTacToePosition implements InterfacePosition {
    // This implementation packs the position into an int
    // The int is between 0 and 4^9-1. This is not very compact, but efficient
    // "Color" is convention, only integers are returned or set.
    // 0-empty, 1-x (cross), 2-o (nought)
    
    // Rightmost 18=9*2 store color (each 2 bits stores 0,1,2; so it is a little lossy)
    // Leftmost 1 bit stores player (1 or 2)

    private int position;
    
    TicTacToePosition() {
        position = 0;
    }

    TicTacToePosition( InterfacePosition pos ) {
        position = (int) pos.getRawPosition();
    }

    @Override public int nC() { return 3; }
    @Override public int nR() { return 3; }

    @Override
    public long getRawPosition() { 
        return position;
    }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if empty, 1 if x(cross), 2 if o(nought)
        int  iC  = iPos.iC();
        int  iR  = iPos.iR();
        return getColor( iC, iR);
    }

    public int getColor( int iC, int iR ) { // 0 if empty, 1 if x(cross), 2 if o(nought)
        int powerOf4 = 3*iR+iC;
        return (position >>> ( 2*powerOf4 )) & 3;
    }

    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // 0 if empty, 1 if x(cross), 2 if o(nought)
        int  iC  = iPos.iC();
        int  iR  = iPos.iR();
        if ( getColor(iC,iR) != 0 ) { 
            System.err.println("Error: This position ("+iC+","+iR+") is already filled.");
        } else {
            int powerOf4 = 3*iR+iC;
            position += color << ( 2*powerOf4 );
        }
    }

    @Override
    public int isWinner() {
        //      If winner, determine that and return winner, 
        //      else if draw, return 0
        //      else if neither winner nor draw, return -1

        // Check every row and column for three in a row
        boolean isFull = true;
        for (    int i0=0; i0<3; i0++) {
            if (getColor(i0,0)==0) isFull = false;
            int checkColmn = getColor(i0, 0); // getColor returns 0 if empty square. Else, it return player
            int checkRow   = getColor( 0,i0);
            for (int i1=1; i1<3; i1++) {
                if (getColor(i0,i1)==0) isFull = false;
                if (checkColmn > 0 && checkColmn != getColor(i0,i1)) checkColmn = 0;
                if (checkRow   > 0 && checkRow   != getColor(i1,i0)) checkRow   = 0;
            }
            if     (checkColmn > 0 )   return checkColmn;
            if     (checkRow   > 0 )   return checkRow  ;
        }

        // Check both diagonals for three in a row
        int checkDiagA = getColor( 0, 0);
        int checkDiagB = getColor( 0, 2);
        for (int i1=1; i1<3; i1++) {
            if (checkDiagA > 0 && checkDiagA != getColor(i1,  i1)) checkDiagA = 0;
            if (checkDiagB > 0 && checkDiagB != getColor(i1,2-i1)) checkDiagB = 0;
        }
        if     (checkDiagA > 0 )   return checkDiagA;
        if     (checkDiagB > 0 )   return checkDiagB;

        if     (isFull         )   return          0; // Tie
        else                       return         -1;
    }

    @Override
    public void reset() {
        position = 0;
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(TicTacToePosition::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            int  currentPlayer = getPlayer();
            if ( currentPlayer != iPlayer ) {
                position ^= 1L << 31;
            }
        }
    }

    @Override
    public int getPlayer() {
        return ((int)(position>>>31))+1;
    }

    @Override
    public float valuePosition() {
        // Not yet used
        return 0/0;
    }

    @Override
    public int getChipCount() {
        // Not yet used
        return 0/0;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // Not yet used
        return 0/0;
    }

}

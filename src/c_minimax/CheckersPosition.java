package c_minimax;

//author: Gary Kalmanovich; rights reserved

public class CheckersPosition implements InterfacePosition {

    // This implementation is designed for at most 8 columns by 8 rows
    // It packs the entire position into a single long
    
    // Rightmost 51=cap(log2(3^32)) bits are used to store a 32 digit trinary (0..2) number
    // Leftmost 1 bit stores the player 1 or 2

    private long position = 0;
    private int nC = 0;
    private int nR = 0;
    private int[] powerOfThree = new int[32];

    CheckersPosition( int nC, int nR) {
        position = 0;
        this.nC = nC;
        this.nR = nR;
        setPowerOfThree();
    }

    CheckersPosition( InterfacePosition pos ) {
        position = pos.getRawPosition();
        nC       = pos.nC();
        nR       = pos.nR();
        setPowerOfThree();
    }
    
    private void setPowerOfThree() { 
        powerOfThree[0] = 1;
        for( int iPow = 1 ; iPow < 32 ; iPow++ ) {
            powerOfThree[iPow] = 3*powerOfThree[iPow-1];
        }
    }

    @Override public int nC() { return nC; }
    @Override public int nR() { return nR; }

    @Override public long getRawPosition() { return position; }

    @Override
    public int getColor( InterfaceIterator iPos ) { // 0 if transparent, 1 if white, 2 if black
        return getColor( iPos.iC(), iPos.iR() );
    }

    private int getColor( int iC, int iR ) { // 0 if transparent, 1 if white, 2 if black
        return ( (int) ( position / powerOfThree[(8*iR+iC)/2] ) ) & 3;
    }

    @Override
    public void setColor( InterfaceIterator iPos, int color ) { // color is 1 if red, 2 if yellow
        setColor( iPos.iC(), iPos.iR(), color );
    }

    private void setColor( int iC, int iR, int color ) { // 0 if transparent, 1 if white, 2 if black
        int oldColor = getColor(iC,iR);
        position += (color-oldColor) * powerOfThree[(8*iR+iC)/2];
    }

    @Override
    public void setPlayer(int iPlayer) { // Only 1 or 2 are valid
        if ( !(0<iPlayer && iPlayer<3) ) {
            System.err.println("Error(Connect4Position::setPlayer): iPlayer ("+iPlayer+") out of bounds!!!");
        } else {
            int  currentPlayer = getPlayer();
            if ( currentPlayer != iPlayer ) {
                position ^= 1L << 63;
            }
        }
    }

    @Override
    public int getPlayer() {
        return ((int)(position>>>63))+1;
    }

    @Override
    public int isWinner(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int isWinner() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float valuePosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getChipCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getChipCount(InterfaceIterator iPos) {
        // TODO Auto-generated method stub
        return 0;
    }

}

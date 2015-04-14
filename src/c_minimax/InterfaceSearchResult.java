package c_minimax;

//author: Gary Kalmanovich; rights reserved

interface InterfaceSearchResult{ 
    // Anything that is related to the result of a search
    // For example,
    InterfaceIterator getBestMoveSoFar();
    float    getBestScoreSoFar();
    void     setBestMoveSoFar( InterfaceIterator newMove, float newScore );
    void     setIsResultFinal( boolean isFinal );
    boolean  isResultFinal();
    float    getOpponentBestScoreOnPreviousMoveSoFar();                    // For alpha-beta pruning
    void     setOpponentBestScoreOnPreviousMoveSoFar( float scoreToBeat ); // For alpha-beta pruning
    int      getClassStateCompacted();
    void     setClassStateFromCompacted(int compacted);
    // Note, not all of these need to be fully implemented. 
    // Ones that are not utilized can be simply empty shells. 
}
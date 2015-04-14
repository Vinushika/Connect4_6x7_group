package c_minimax;

//author: Gary Kalmanovich; rights reserved

interface InterfaceSearchContext{ 
    // Anything that is related to bounds on strategy calculations.
    // In particular this class is connected to a search of a particular position
    //  and contains the parameters of the search
    // For example,
    int      getCurrentDepth();
    void     setCurrentDepth(             int    depth );
    int      getMinDepthSearchForThisPos();
    void     setMinDepthSearchForThisPos( int minDepth );
    int      getMaxDepthSearchForThisPos();
    void     setMaxDepthSearchForThisPos( int maxDepth );
    long     getMaxSearchTimeForThisPos();
    void     setMaxSearchTimeForThisPos( long maxTime  );
    // Note, not all of these need to be fully implemented. 
    // Ones that are not utilized can be simply empty shells. 
}
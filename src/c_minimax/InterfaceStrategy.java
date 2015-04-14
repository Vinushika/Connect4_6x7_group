package c_minimax;

//author: Gary Kalmanovich; rights reserved

public interface InterfaceStrategy {
    InterfaceSearchResult getBestMove(InterfacePosition position, InterfaceSearchContext context); // Return info is in context
    void setContext( InterfaceSearchContext strategyContext );
    InterfaceSearchContext getContext();
}

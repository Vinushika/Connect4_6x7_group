package c_minimax;

//author: Gary Kalmanovich; rights reserved

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

class ThreadStrategy extends NotifyingThread {

    private InterfaceStrategy strategy;
    private InterfacePosition position;
    private InterfaceSearchContext context;
    private InterfaceSearchResult  result = null;

    ThreadStrategy( InterfaceStrategy strategy, InterfacePosition position, InterfaceSearchContext context ) {
        this.strategy = strategy;
        this.position = position;
        this.context  = context ;
    }

    InterfaceSearchContext getContext() { return context; } // 
    InterfaceSearchResult  getResult()  { return result;  } // 
    
    @Override public void doRun() {
        long startTime = System.nanoTime(); // Start the total timing
        result = strategy.getBestMove(position, context);
        //System.out.println("Best move(Thread): c "+bestMove.iC()+", r "+bestMove.iR()+", for player "+player);
        long endTime  = System.nanoTime(); // Finish the total timing
        float timeElapsed = (endTime-startTime)/1000000.0f; // milliseconds
        System.out.println("Move time:" + timeElapsed + 
                " ms. Score: " + result.getBestScoreSoFar() + 
                " Player: " + position.getPlayer());
        if ( timeElapsed < 1000 ) { // milliseconds
            try {
                sleep(1000-(int)timeElapsed); // make sure we wait a total of at least 100 milliseconds
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

// Using thread code from 
//   http://stackoverflow.com/questions/702415/how-to-know-if-other-threads-have-finished

interface ThreadCompleteListener {
    void notifyOfThreadComplete(final Thread thread);
}

abstract class NotifyingThread extends Thread {
    private final Set<ThreadCompleteListener> listeners
                     = new CopyOnWriteArraySet<ThreadCompleteListener>();
    public final void addListener(final ThreadCompleteListener listener) {
      listeners.add(listener);
    }
    public final void removeListener(final ThreadCompleteListener listener) {
      listeners.remove(listener);
    }
    private final void notifyListeners() {
      for (ThreadCompleteListener listener : listeners) {
        listener.notifyOfThreadComplete(this);
      }
    }
    @Override
    public final void run() {
      try {
        doRun();
      } finally {
        notifyListeners();
      }
    }
    public abstract void doRun();
  }

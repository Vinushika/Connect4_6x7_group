package c_minimax;

//author: Gary Kalmanovich; rights reserved

import javafx.stage.Stage;

public interface InterfaceView {
    void start(Stage primaryStage);
    void performMove( int i0C, int i0R, int i1C, int i1R, int iPlayer ); // i0-from; i1-to;
    int  nC();
    int  nR();
}

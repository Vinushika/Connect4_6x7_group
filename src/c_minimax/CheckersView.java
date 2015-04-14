package c_minimax;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//author: Gary Kalmanovich; rights reserved
//  This code is being loosely based on the Connect4View code that is based on
//  https://glyphsoft.wordpress.com/2012/09/23/javafx-game-connect-four/

public class CheckersView implements InterfaceView {

    private int nSquareSize = 84;
    private int nC = 8;//4;//
    private int nR = 8;//4;//

    private final GridPane gridpane = new GridPane();
    private InterfaceControl controller;
    
    private int currentPlayer = 1; // First player (White) has the first move
    
    CheckersView(InterfaceControl controller) {
        this.controller = controller;
        controller.setView(this);
    }

    @Override public int nC() { return nC; }
    @Override public int nR() { return nR; }

    @Override
    public void start(Stage primaryStage) {
        // Note: this method does not override Application::start()
        // It could if the class implemented Application. However, it does not.
         
        final BorderPane root = new BorderPane();
        primaryStage.setTitle("JavaFX Checkers");
        primaryStage.setResizable(true);
         
        final Button newGameButton = new Button("New Game");
        newGameButton.setOnAction((event) -> {
            //resetGame();
        });
         
        Scene scene = new Scene(root, 750, 690, true);
        scene.setFill(Color.BLACK);
        //scene.getStylesheets().add("net/glyphsoft/styles.css");
         
        gridpane.setTranslateY(nSquareSize*5/100);
        gridpane.setAlignment(Pos.CENTER);

        for ( int iC = 0; iC < nC; iC++ ) {
            gridpane.getColumnConstraints().add(
                new ColumnConstraints(nSquareSize,nSquareSize,Double.MAX_VALUE));
            gridpane.getRowConstraints().add(
                new RowConstraints(   nSquareSize,nSquareSize,Double.MAX_VALUE)); 
        }
         
        createGrids();
         
        root.setCenter(gridpane);
         
        HBox topRow = new StrategyChoice(controller,Color.BLACK);
        Label newGameLabel = new Label("  Reset to"); 
        topRow.getChildren().addAll(newGameLabel, newGameButton);
        root.setTop(topRow);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //Create Grids
    private void createGrids() {
        gridpane.getChildren().clear();
        for(     int iR=0; iR<nR; iR++ ) {
            for( int iC=0; iC<nC; iC++ ) {

                // Auxiliary data
                DragContext dragContext = new DragContext();

                Rectangle rect = new Rectangle(nSquareSize,nSquareSize);
                rect.setFill( (iC+iR)%2==1 ? Color.BLUE : Color.LIGHTGRAY );
                //rect.setStroke(Color.BLUE);

                // Circle disk are checkers pieces
                final Circle disk = new Circle(nSquareSize*2/5);
                diskMoveTo( disk, iC, iR );
                if ( (iC+iR)%2==1 ) {
                    if ( iR < nR/2 - 1 ) { // White piece
                        disk.setFill(Color.BLACK);
                        gridpane.add(disk, 0, 0); 
                    } else if ( iR > nR/2 ) { // Black piece
                        disk.setFill(Color.WHITE);
                        gridpane.add(disk, 0, 0); 
                    }
                }

                disk.setOnMouseEntered(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //disk.setFill(Color.WHITE);
                        //if(playerColorProperty.get()==Color.RED){
                        //    diskPreview.setFill(Color.RED);
                        //}else{
                        //    diskPreview.setFill(Color.YELLOW);
                        //}
                    }
                });
                 
                disk.setOnMouseExited(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        //disk.setFill(Color.TRANSPARENT);
                    }
                });
                 
                disk.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        dragContext.lastSceneX   = mouseEvent.getSceneX();
                        dragContext.lastSceneY   = mouseEvent.getSceneY();
                        dragContext.preDragNodeX = disk.getTranslateX()  ;
                        dragContext.preDragNodeY = disk.getTranslateY()  ;
                        disk.toFront();
                    }
                });
                 
                disk.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        disk.setTranslateX( dragContext.atDragNodeX( mouseEvent.getSceneX() ) );
                        disk.setTranslateY( dragContext.atDragNodeY( mouseEvent.getSceneY() ) );
                    }
                });
                 
                disk.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        checkIfValidAndMove( dragContext, disk, mouseEvent.getSceneX(), mouseEvent.getSceneY() );
                    }
                });

                gridpane.add(rect, iC, iR); 
                rect.toBack();
            }
        }
    }
    
    private void checkIfValidAndMove( DragContext dragContext, Node disk, double newX, double newY ) {
        int oldC = dragContext.iC();
        int oldR = dragContext.iR();
        int newC = dragContext.iC(newX);
        int newR = dragContext.iR(newY);
        if ( 0<=newC && newC<nC && 0<=newR && newR<nR && (newC+newR)%2==1) {
            if ( ((Circle)disk).fillProperty().get().equals(Color.WHITE) && currentPlayer==1 ) {
                if        (newR == oldR-1 && Math.abs(newC-oldC) == 1 && 
                                                   getColorAt(disk,newC,newR)==0) { // Simple white move
                    diskMoveTo( disk, newC, newR );
                    currentPlayer = 2;
                } else if (newR == oldR-2 && Math.abs(newC-oldC) == 2 && 
                                                   getColorAt(disk,newC,newR)==0 &&
                                                   getColorAt(disk,(newC+oldC)/2,newR+1)==2) { // Capture by white move
                    diskMoveTo( disk, newC, newR );
                    deleteDiskAt(disk,(newC+oldC)/2,newR+1);
                    currentPlayer = 2;
                } else {
                    diskMoveTo( disk, oldC, oldR );
                }
            } else if ( ((Circle)disk).fillProperty().get().equals(Color.BLACK) && currentPlayer==2 ) {
                if        (newR == oldR+1 && Math.abs(newC-oldC) == 1 && 
                                                   getColorAt(disk,newC,newR)==0) { // Simple black move
                    diskMoveTo( disk, newC, newR );
                    currentPlayer = 1;
                } else if (newR == oldR+2 && Math.abs(newC-oldC) == 2 && 
                        getColorAt(disk,newC,newR)==0 &&
                        getColorAt(disk,(newC+oldC)/2,newR-1)==1) { // Capture by black move
                    diskMoveTo( disk, newC, newR );
                    deleteDiskAt(disk,(newC+oldC)/2,newR-1);
                    currentPlayer = 1;
                } else {
                    diskMoveTo( disk, oldC, oldR );
                }
            } else {
                diskMoveTo( disk, oldC, oldR );
            }
        } else {
            diskMoveTo( disk, oldC, oldR );
        }
    }

    private void diskMoveTo( Node disk, int iC, int iR ) {
        disk.setTranslateX( nSquareSize*(iC+10./100) );
        disk.setTranslateY( nSquareSize*(iR+ 0./100) );
    }
    
    private int calcC( double locationX ) { return (int)(locationX/nSquareSize-10./100+.5); }
    private int calcR( double locationY ) { return (int)(locationY/nSquareSize- 0./100+.5); }

    private final class DragContext {
        public double preDragNodeX;
        public double preDragNodeY;
        public double   lastSceneX;
        public double   lastSceneY;
        double atDragNodeX( double newSceneX ) { return newSceneX-lastSceneX+preDragNodeX; }
        double atDragNodeY( double newSceneY ) { return newSceneY-lastSceneY+preDragNodeY; }
        int iC() { return iC(lastSceneX);} // iC before move
        int iR() { return iR(lastSceneY);} // iR before move
        int iC( double newSceneX ) { return calcC(atDragNodeX(newSceneX));}
        int iR( double newSceneY ) { return calcR(atDragNodeY(newSceneY));}
    }

    private void deleteDiskAt( Node skipNode, int iC, int iR ) { // delete node, but not the skipNode
        Circle foundNode = getDiskAt(skipNode, iC, iR);// but, do not check the skipNode
        if (foundNode != null) gridpane.getChildren().remove(foundNode);
    }

    private int getColorAt( Node skipNode, int iC, int iR ) { // return 0 if empty; 1 if white; 2 if black
        Circle foundNode = getDiskAt(skipNode, iC, iR);// but, do not check the skipNode
        if (foundNode == null) return 0; // Did not find a disk
        else if (foundNode.fillProperty().get().equals(Color.WHITE) ) return 1;
        else if (foundNode.fillProperty().get().equals(Color.BLACK) ) return 2;
        else {
            System.err.println("Checkers can only recognize disks that are WHITE or BLACK. This fill property is not compliant");
            return -1;
        }
    }

    private Circle getDiskAt( Node skipNode, int iC, int iR ) { // return disk, but not the skipNode disk
        ObservableList<Node> listOfNodes = gridpane.getChildren();
        for(Node node : listOfNodes) {
            if(     calcC(node.getTranslateX()) == iC && 
                    calcR(node.getTranslateY()) == iR &&
                    node instanceof Circle              &&
                    node != skipNode                       ) {
                return (Circle)node;
            }
        }
        return null; // Did not find
    }

    @Override
    public void performMove(int i0C, int i0R, int i1C, int i1R, int iPlayer) {
        // TODO Auto-generated method stub

    }

}

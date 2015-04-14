package c_minimax;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
 
 
/**
//author: Gary Kalmanovich; rights reserved
 * Original version of this code was taken from:
 * https://glyphsoft.wordpress.com/2012/09/23/javafx-game-connect-four/
 * thus, original @author mark_anro
 */
public class Connect4View implements InterfaceView{
 
    /**
     * @param args the command line arguments
     */
    private int nSquareSize = 84;// Originally this was 100
    private int nC = 7;//7;//
    private int nR = 6;//6;//
     
    private SimpleObjectProperty<Color> playerColorProperty = new SimpleObjectProperty<Color>(Color.RED);
    private final GridPane gridpane = new GridPane();
    private InterfaceControl controller;

    Connect4View(InterfaceControl controller) {
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
        //final GridPane gridpane = new GridPane(); // Now a class variable
        primaryStage.setTitle("JavaFX Connect Four");
        primaryStage.setResizable(true);
         
        final Button newGameButton = new Button("New Game");
        newGameButton.setOnAction((event) -> {
            resetGame();
        });
         
        Scene scene = new Scene(root, 750, 690, true);
        scene.setFill(Color.BLACK);
        //scene.getStylesheets().add("net/glyphsoft/styles.css");
         
        gridpane.setTranslateY(nSquareSize*2/5);
        gridpane.setAlignment(Pos.CENTER);

        for ( int iC = 0; iC < nC; iC++ ) {
            gridpane.getColumnConstraints().add(
                new ColumnConstraints(nSquareSize,nSquareSize,Double.MAX_VALUE));
            gridpane.getRowConstraints().add(
                new RowConstraints(   nSquareSize,nSquareSize,Double.MAX_VALUE)); 
        }
         
        createGrids(gridpane);
         
        root.setCenter(gridpane);
         
        //DropShadow effect = new DropShadow();
        //effect.setColor(Color.BLUE);
        //addCellButton.setEffect(effect);
         
        //addCellButton.setTranslateY(10);
        //addCellButton.setTranslateX(10);
         
        //root.setTop(addCellButton);
         
        //addCellButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
        //    @Override
        //    public void handle(MouseEvent arg0) {
        //        addGrid(gridpane);
        //    }
        //});
        HBox topRow = new StrategyChoice(controller,Color.BLACK);
        Label newGameLabel = new Label("  Reset to"); 
        topRow.getChildren().addAll(newGameLabel, newGameButton);
        root.setTop(topRow);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
     
    ////Add Column and Row
    //private void addGrid(final GridPane gridpane){
    //    gridpane.getColumnConstraints().addAll(new ColumnConstraints(nSquareSize,nSquareSize,Double.MAX_VALUE));
    //    gridpane.getRowConstraints()   .addAll(new RowConstraints(   nSquareSize,nSquareSize,Double.MAX_VALUE));
    //    createGrids(gridpane);
    //}
     
    //Create Grids
    private void createGrids(final GridPane gridpane){
        gridpane.getChildren().clear();
        for(     int iR=0; iR<nR; iR++ ) {
            for( int iC=0; iC<nC; iC++ ) {
                     
                Rectangle rect = new Rectangle(nSquareSize,nSquareSize);
                Circle circ = new Circle(  nSquareSize*47/100);
                circ.centerXProperty().set(nSquareSize   /  2);
                circ.centerYProperty().set(nSquareSize   /  2);
                Shape cell = Path.subtract(rect, circ);
                cell.setFill(Color.BLUE);
                cell.setStroke(Color.BLUE);
                cell.setOpacity(.8);
                DropShadow effect = new DropShadow();
                effect.setSpread(.2);
                effect.setRadius(nSquareSize/4);
                effect.setColor(Color.BLUE);
                cell.setEffect(effect);

                // In this architecture, diskPreview are on the bottom while Disk 
                //   are up top at the beginning of the game 
    
                final Circle diskPreview = new Circle(nSquareSize*2/5);
                diskPreview.setOpacity(.5);
                diskPreview.setFill(Color.TRANSPARENT);
    
                diskPreview.setOnMouseEntered(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent arg0) {
                        //diskPreview.setFill(Color.WHITE);
                        //if(playerColorProperty.get()==Color.RED){
                        //    diskPreview.setFill(Color.RED);
                        //}else{
                        //    diskPreview.setFill(Color.YELLOW);
                        //}
                    }
                });
                 
                diskPreview.setOnMouseExited(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent arg0) {
                        diskPreview.setFill(Color.TRANSPARENT);
                    }
                });
                 
                final Circle disk = new Circle(nSquareSize*2/5);
                disk.fillProperty().bind(playerColorProperty);
                disk.setOpacity(.49);
                disk.setTranslateY(-(nSquareSize*(iR+1)));

                disk.setOnMouseEntered(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent arg0) {
                        //diskPreview.setFill(Color.WHITE);
                        //if(playerColorProperty.get()==Color.RED){
                        //    diskPreview.setFill(Color.RED);
                        //}else{
                        //    diskPreview.setFill(Color.YELLOW);
                        //}
                    }
                });
                 
                disk.setOnMouseExited(new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent arg0) {
                        diskPreview.setFill(Color.TRANSPARENT);
                    }
                });
                 
                disk.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        if(disk.getTranslateY()!=0) {
                            performMove(disk);
                        }
                    }
                });
                 
                diskPreview.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        if(disk.getTranslateY()!=0) {
                            performMove(disk);
                        }
                    }
                });

                StackPane stack = new StackPane();
                 
                stack.getChildren().addAll(cell, disk, diskPreview); // disk before diskPreview so that it is found first.
                 
                gridpane.add(stack, iC, iR); 
             
                if(iR==nR-1){
                    stack.setEffect(new Reflection());
                }
            }
        }
    }
    
    private void resetGame() {
        ObservableList<Node> listOfNodes = gridpane.getChildren();
        for(Node node : listOfNodes) {
            if(  node instanceof StackPane                   ) {
                ObservableList<Node> listOfStackNodes = ((StackPane)node).getChildren();
                for(Node nodeStack : listOfStackNodes) {
                    if(          nodeStack instanceof Circle &&
                       ((Circle) nodeStack).getOpacity() < .495) { // DiskPreview has opacity of .50 vs .49
                        nodeStack.setTranslateY(-(nSquareSize*(GridPane.getRowIndex(node)+1)));
                        ((Circle)nodeStack).fillProperty().bind(playerColorProperty);
                        break;
                    }
                }
            }
        }
        controller.resetGame();
        playerColorProperty.set(Color.RED);
        controller.onMove();
    }
    
    private void setColor( Circle disk, Color colorFrom, Color colorTo ) {
        playerColorProperty.set(colorTo);
        disk.fillProperty().bind(new SimpleObjectProperty<Color>(colorFrom));
        int iPlayer = ( colorFrom == Color.RED ) ? 1 : 2 ; // This is the player that made the move
        controller.onMove( 0, 0,    // Zeros ( 0 ) are dummy, not used
                GridPane.getColumnIndex(disk.getParent()), 
                GridPane.getRowIndex(   disk.getParent()), iPlayer);
    }
    
    @Override
    public void performMove( int dummy1, int dummy2, int iC, int iR, int dummy3 ) {
        //  performMoveUnchecked( getDisk( iC, iR ) ); // I don't fully understand this. However, in 
        Platform.runLater(()->{                        // javaFX, UI changes that come from a different 
            performMoveUnchecked( getDisk( iC, iR ) ); // thread are not allowed. But, this runLater()
            });                                        // wrap somehow fixes it. See, http://stackoverflow.com/questions/17850191/why-am-i-getting-java-lang-illegalstateexception-on-javafx
    }
    
    private void performMove( Circle disk ) {
        if ( !controller.isBlockManualMove() ) {
            performMoveUnchecked( disk );
        }
    }
    
    private void performMoveUnchecked( Circle disk ) {
        final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(300), disk);
        translateTransition.setToY(0);
        translateTransition.play();
        if(playerColorProperty.get()==Color.RED) {
            setColor( disk, Color.RED  , Color.YELLOW );
        } else {
            setColor( disk, Color.YELLOW, Color.RED   );
        }
    }
    
    private Circle getDisk( int iC, int iR ) {
        ObservableList<Node> listOfNodes = gridpane.getChildren();
        Node result = null;
        for(Node node : listOfNodes) {
            if(     GridPane.getRowIndex(   node) == iR && 
                    GridPane.getColumnIndex(node) == iC &&
                    node instanceof StackPane                ) {
                ObservableList<Node> listOfStackNodes = ((StackPane)node).getChildren();
                for(Node nodeStack : listOfStackNodes) {
                    if(          nodeStack instanceof Circle &&
                       ((Circle) nodeStack).getOpacity() < .495) { // DiskPreview has opacity of .50 vs .49
                        result = nodeStack;
                        break;
                    }
                }
            }
            if(result!=null) break; // Stop iterating
        }
        if (result == null) System.err.println("ERROR: Did not find a Circle Node at row: "+iR+" and column: "+iC+" !!!");
        return (Circle) result;
    }
} 
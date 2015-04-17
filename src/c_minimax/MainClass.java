package c_minimax;

//author: Gary Kalmanovich; rights reserved

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClass extends Application {

	static enum Game {
		TIC_TAC_TOE, CONNECT4, CHECKERS
	}

	static InterfaceControl controller;
	static InterfaceView viewer;

	public static void main(String[] args) {
		Game myGame = Game.CONNECT4;// Game.CHECKERS ;// Game.TIC_TAC_TOE ;//

		if (myGame == Game.CONNECT4) {
			controller = new Connect4Control();
			viewer = new Connect4View(controller);
		} else if (myGame == Game.CHECKERS) {
			controller = new CheckersControl();
			viewer = new CheckersView(controller);
		} else if (myGame == Game.TIC_TAC_TOE) {
			controller = new TicTacToeControl();
			viewer = new TicTacToeView(controller);
		} else
			System.err.println("This game type is not supported");

		// System.setProperty("prism.dirtyopts", "false");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			viewer.start(primaryStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

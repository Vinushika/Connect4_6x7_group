package c_minimax;

//Original code from https://gist.github.com/jewelsea/5115901
//Since modified by
//author: Gary Kalmanovich; rights reserved

/**
* Copyright 2013 John Smith
*
* This file is part of Jewelsea Tic-Tac-Toe.
*
* Jewelsea Tic-Tac-Toe is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Jewelsea Tic-Tac-Toe is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Jewelsea Tic-Tac-Toe.  If not, see <http://www.gnu.org/licenses/>.
*
* Contact details: http://jewelsea.wordpress.com
*
* icon image license => creative commons with attribution:
*   http://creativecommons.org/licenses/by/3.0/
* icon image creator attribution:
*   http://www.doublejdesign.co.uk/products-page/icons/origami-colour-pencil
*/

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

class StrategyChoice extends HBox {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    StrategyChoice(InterfaceControl controller, Color color) {
        //Label label0 = new Label("Strategy: ");  label0.setTextFill(color);
        Label label1 = new Label("player1:"  );  label1.setTextFill(color);
        Label label2 = new Label("player2:"  );  label2.setTextFill(color);
        final String [] choices = new String[] { "Manual", "StrtgyA", "StrtgyB" };
        ChoiceBox cb1 = new ChoiceBox(FXCollections.observableArrayList( choices ) ); 
        ChoiceBox cb2 = new ChoiceBox(FXCollections.observableArrayList( choices ) ); 
        cb1.getSelectionModel().select(controller.getStrategy(1)); // Set to first  player's strategy
        cb2.getSelectionModel().select(controller.getStrategy(2)); // Set to second player's strategy
        cb1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                    controller.setStrategy( 1, new_value.intValue() );
            }
        } );
        cb2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                    controller.setStrategy( 2, new_value.intValue() );
            }
        } );
        getChildren().addAll(label1,cb1,label2,cb2);
    }
}


package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingView {
    private static Stage stage;
    
    public void show() {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("loading-background");
        
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(100, 100);
        
        Label label = new Label("Conectando al servidor...");
        label.getStyleClass().add("loading-text");
        
        layout.getChildren().addAll(progress, label);
        
        Scene scene = new Scene(layout, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/Styles/game.css").toExternalForm());
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void close() {
        if (stage != null) {
            stage.close();
            stage = null;
        }
    }
}
package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;

public class DisconnectedView {
    private Stage primaryStage;

    public DisconnectedView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("disconnected-root");

        Label message = new Label("DESCONEXIÓN DEL SERVIDOR");
        message.setFont(FontUtils.cargarFuenteBaloo(36));
        message.getStyleClass().add("disconnected-message");
        message.setWrapText(true);

        ImageView warningIcon = new ImageView(new Image(getClass().getResource("/icons/Warning.png").toExternalForm()));
        warningIcon.setPreserveRatio(true);
        warningIcon.getStyleClass().add("warning-icon");
        warningIcon.setFitWidth(200);

        root.getChildren().addAll(message, warningIcon);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/disconnected.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Error de conexión");

        // Ajuste responsive
        warningIcon.fitWidthProperty().bind(scene.widthProperty().divide(4));
    }
}
package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class DisconnectedView {

    public void show() {
        // Configuración del layout principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("disconnected-root");

        // Mensaje principal
        Label message = new Label("DESCONEXIÓN DEL SERVIDOR");
        message.setFont(loadCustomFont(36));
        message.getStyleClass().add("disconnected-message");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);

        // Icono de advertencia
        ImageView warningIcon = new ImageView(new Image(getClass().getResource("/icons/Warning.png").toExternalForm()));
        warningIcon.setPreserveRatio(true);
        warningIcon.getStyleClass().add("warning-icon");
        warningIcon.setFitWidth(200); // Tamaño base

        root.getChildren().addAll(message, warningIcon);

        // Configuración de la escena
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/disconnected.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Error de conexión");
        stage.setScene(scene);
        stage.show();

        // Ajustes responsive
        warningIcon.fitWidthProperty().bind(scene.widthProperty().divide(4));
    }

    private Font loadCustomFont(double size) {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Baloo2-ExtraBold.ttf");
            if (is != null) {
                Font font = Font.loadFont(is, size);
                if (font != null) {
                    return font;
                }
            }
            // Fallback seguro
            return Font.font("Arial", FontWeight.BOLD, size);
        } catch (Exception e) {
            System.err.println("Error loading font: " + e.getMessage());
            return Font.font("Arial", FontWeight.BOLD, size);
        }
    }
}
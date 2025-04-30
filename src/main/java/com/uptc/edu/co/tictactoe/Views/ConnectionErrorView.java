package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

import java.io.InputStream;

public class ConnectionErrorView {

    public void show() {
        Font titleFont = loadCustomFont(38);
        Font subtitleFont = loadCustomFont(20);
        Font buttonFont = loadCustomFont(18);

        // Título
        Label title = new Label("ERROR DE CONEXIÓN");
        title.setFont(titleFont);
        title.setTextFill(Color.web("#FFB3F9"));
        title.setEffect(new DropShadow(10, Color.web("#FF2DF1")));

        // Subtítulo
        Label subtitle = new Label("VERIFICA TU CONEXIÓN A INTERNET");
        subtitle.setFont(subtitleFont);
        subtitle.setTextFill(Color.web("#FFB3F9"));
        subtitle.setEffect(new DropShadow(8, Color.web("#FF2DF1")));

        // Imagen central
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/Icons/ErrorClose.png")));
        icon.setFitWidth(300);
        icon.setFitHeight(300);

        // Botón de reintento
        Button retryButton = new Button("REINTENTAR");
        retryButton.setFont(buttonFont);
        retryButton.getStyleClass().add("neon-button");
        retryButton.setOnAction(e -> {
            // Aquí podrías intentar reconectar o cerrar
            ((Stage) retryButton.getScene().getWindow()).close();
        });

        // Layout
        VBox root = new VBox(25, title, subtitle, icon, retryButton);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("background");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/error.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Error de Conexión");
        stage.setScene(scene);
        stage.show();
    }

    private Font loadCustomFont(double size) {
        try {
            InputStream is = getClass().getResourceAsStream("/Fonts/Baloo2-ExtraBold.ttf");
            if (is != null) {
                Font font = Font.loadFont(is, size);
                if (font != null) return font;
            }
        } catch (Exception e) {
            System.err.println("Error cargando fuente: " + e.getMessage());
        }
        return Font.font("Arial", javafx.scene.text.FontWeight.BOLD, size);
    }
}

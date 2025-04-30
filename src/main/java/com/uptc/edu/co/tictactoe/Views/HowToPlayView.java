package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class HowToPlayView {

    public void show() {
        // Cargar fuentes
        Font titleFont = loadCustomFont(42);
        Font textFont = loadCustomFont(20);
        Font buttonFont = loadCustomFont(18);

        // Título
        Label title = new Label("¿CÓMO JUGAR?");
        title.setFont(titleFont);
        title.getStyleClass().add("how-title");

        // Instrucciones
        Label instructions = new Label(
                "• El juego es para 2 jugadores o contra la PC\n" +
                "• Gana quien alinee 3 símbolos (X u O)\n" +
                "• Puedes jugar en línea o en local\n" +
                "• Usa el mouse para hacer tu jugada");
        instructions.setFont(textFont);
        instructions.getStyleClass().add("how-text");
        instructions.setMaxWidth(500);
        instructions.setWrapText(true);

        // Botón VOLVER
        Button backButton = createNeonButton("VOLVER", buttonFont);
        backButton.setOnAction(e -> ((Stage) backButton.getScene().getWindow()).close());

        // Layout
        VBox root = new VBox(30, title, instructions, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("how-root");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/howtoplay.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Instrucciones - Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
    }

    private Button createNeonButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.getStyleClass().add("neon-button");
        button.setPrefSize(280, 60);

        // Efectos interactivos idénticos a LoginView
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #410445, #2D0230); " +
                           "-fx-text-fill: #FF2DF1;");
            button.setEffect(new DropShadow(15, Color.web("#F6DC43")));
            button.setTranslateY(-2);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("");
            button.setEffect(new DropShadow(10, Color.web("#F6DC43")));
            button.setTranslateY(0);
        });

        return button;
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
        // Fallback si no se encuentra la fuente
        return Font.font("Arial", FontWeight.BOLD, size);
    }
}


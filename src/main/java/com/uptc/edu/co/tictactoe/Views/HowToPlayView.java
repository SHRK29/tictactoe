package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;

public class HowToPlayView {
    private Stage primaryStage;

    public HowToPlayView(Stage appStage) {
        this.primaryStage = appStage;
    }

    public Scene getScene() {
        Font titleFont = FontUtils.cargarFuenteBaloo(42);
        Font textFont = FontUtils.cargarFuenteBaloo(20);
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);

        Label title = new Label("¿CÓMO JUGAR?");
        title.setFont(titleFont);
        title.getStyleClass().add("how-title");

        Label instructions = new Label(
                "• El juego es para 2 jugadores o contra la PC\n" +
                        "• Gana quien alinee 3 símbolos (X u O)\n" +
                        "• Puedes jugar en línea o en local\n" +
                        "• Usa el mouse para hacer tu jugada");
        instructions.setFont(textFont);
        instructions.setMaxWidth(500);
        instructions.setWrapText(true);

        Button backButton = createNeonButton("VOLVER", buttonFont);
        backButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            App.getPrimaryStage().setScene(loginView.getScene());
            WindowUtils.configurarVentanaPantallaCompleta(this.primaryStage);
        });

        VBox root = new VBox(30, title, instructions, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("how-root");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/Styles/howtoplay.css").toExternalForm());
        return scene;
    }

    private Button createNeonButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.setPrefSize(280, 60);
        // Efectos neon originales...
        return button;
    }
}
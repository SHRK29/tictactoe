package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.application.Platform;
import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;

public class LoginView {

    private Scene scene;
    private TextField nameField;
    private Button onlineButton;
    private Button vsPcButton;
    private Button howToPlayButton;
    private Button exitButton;

    public LoginView() {
        // Cargar fuentes con manejo de errores
        Font balooFont = FontUtils.cargarFuenteBaloo(110);
        Font balooFontMedium = FontUtils.cargarFuenteBaloo(22);
        Font balooFontSmall = FontUtils.cargarFuenteBaloo(18);

        VBox mainLayout = new VBox(30);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getStyleClass().add("background");
        mainLayout.setPadding(new Insets(40));

        // Título con fuente Baloo 2
        Text title = new Text("TIC TAC TOE");
        title.setFont(balooFont);
        title.getStyleClass().add("title");
        title.setFill(Color.web("#FF2DF1"));

        // Campo de texto centrado
        nameField = new TextField();
        nameField.setFont(balooFontMedium);
        nameField.getStyleClass().add("name-field");
        nameField.setAlignment(Pos.CENTER);
        nameField.setPromptText("Escribe tu nombre");
        nameField.setMaxWidth(500);

        // Contenedor de botones
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(30);
        buttonGrid.setVgap(25);

        // Crear botones con tamaño consistente
        onlineButton = createStyledButton("JUGAR ONLINE", balooFontSmall);
        vsPcButton = createStyledButton("JUGAR CONTRA PC", balooFontSmall);
        howToPlayButton = createStyledButton("¿CÓMO JUGAR?", balooFontSmall);
        exitButton = createStyledButton("SALIR", balooFontSmall);

        // Distribución en grid 2x2
        buttonGrid.addRow(0, onlineButton, vsPcButton);
        buttonGrid.addRow(1, howToPlayButton, exitButton);

        // Añadir componentes al layout principal
        mainLayout.getChildren().addAll(title, nameField, buttonGrid);
        VBox.setMargin(nameField, new Insets(0, 0, 30, 0));

        scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/login.css").toExternalForm());

        // Configurar acciones de los botones
        configurarAccionesBotones();
    }

    private void configurarAccionesBotones() {

        howToPlayButton.setOnAction(e -> {
            HowToPlayView howToPlayView = new HowToPlayView();
            App.cambiarEscena(howToPlayView.getScene(), "Cómo Jugar - Tic Tac Toe");
        });

        // Acción para botón Jugar contra PC
        vsPcButton.setOnAction(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "Jugador 1"; // Nombre por defecto
            }
            GameViewOffline gameView = new GameViewOffline(playerName);
            App.cambiarEscena(gameView.getScene(), "Tic Tac Toe - Modo Local");
        });

        // Acción para botón Jugar Online (ejemplo)
        onlineButton.setOnAction(e -> {
            // Implementación para modo online
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "Jugador Online";
            }
            // Aquí iría la lógica para el modo online
        });

        // Acción para botón Salir
        exitButton.setOnAction(e -> Platform.exit());
    }

    private Button createStyledButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.getStyleClass().add("neon-button");
        button.setPrefSize(280, 60);

        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(15, Color.web("#F6DC43")));
            button.setTranslateY(-2);
        });

        button.setOnMouseExited(e -> {
            button.setEffect(new DropShadow(10, Color.web("#F6DC43")));
            button.setTranslateY(0);
        });

        button.setOnMousePressed(e -> {
            button.setEffect(new DropShadow(5, Color.web("#F6DC43")));
            button.setTranslateY(1);
        });

        button.setOnMouseReleased(e -> {
            button.setEffect(new DropShadow(10, Color.web("#F6DC43")));
            button.setTranslateY(0);
        });

        return button;
    }

    public Scene getScene() {
        return scene;
    }

    // Getters para los componentes (si son necesarios)
    public TextField getNameField() {
        return nameField;
    }

    public Button getOnlineButton() {
        return onlineButton;
    }

    public Button getVsPcButton() {
        return vsPcButton;
    }

    public Button getHowToPlayButton() {
        return howToPlayButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
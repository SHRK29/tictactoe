package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.application.Platform;
import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;

public class LoginView {
    private Scene scene;
    private TextField nameField;
    private Button onlineButton;
    private Button offlineButton;
    private Button howToPlayButton;
    private Button exitButton;

    public LoginView() {
        createView();
    }

    private void createView() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #410445;");

        // Título
        Text titleText = new Text("TIC TAC TOE");
        titleText.setFont(FontUtils.cargarFuenteBaloo(64));
        titleText.setFill(Color.web("#FF2DF1"));
        titleText.setEffect(createNeonEffect(Color.web("#FF2DF1")));

        // Campo de nombre
        nameField = new TextField();
        nameField.setPromptText("Escribe tu nombre");
        nameField.getStyleClass().add("name-field");
        nameField.setMaxWidth(400);
        nameField.setFont(FontUtils.cargarFuenteBaloo(20));

        // Contenedor principal de botones
        VBox buttonContainer = new VBox(30);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(20, 0, 0, 0));

        // Contenedor para botones de juego (fila superior)
        HBox gameButtonsBox = new HBox(20);
        gameButtonsBox.setAlignment(Pos.CENTER);
        onlineButton = createStyledButton("JUGAR ONLINE", e -> handleOnlineMode());
        offlineButton = createStyledButton("JUGAR CONTRA PC", e -> handleOfflineMode());
        gameButtonsBox.getChildren().addAll(onlineButton, offlineButton);

        // Contenedor para botones de utilidad (fila inferior)
        HBox utilityButtonsBox = new HBox(20);
        utilityButtonsBox.setAlignment(Pos.CENTER);
        howToPlayButton = createStyledButton("¿COMO JUGAR?", e -> handleHowToPlay());
        exitButton = createStyledButton("SALIR", e -> Platform.exit());
        utilityButtonsBox.getChildren().addAll(howToPlayButton, exitButton);

        buttonContainer.getChildren().addAll(gameButtonsBox, utilityButtonsBox);

        root.getChildren().addAll(titleText, nameField, buttonContainer);
        scene = new Scene(root, 800, 600);
        
        String cssPath = "/Styles/login.css";
        if (getClass().getResource(cssPath) != null) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }

    private Button createStyledButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("neon-button");
        button.setFont(FontUtils.cargarFuenteBaloo(20));
        button.setOnAction(handler);
        return button;
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(color);
        neonEffect.setSpread(0.5);
        neonEffect.setRadius(20);
        Bloom bloom = new Bloom(0.6);
        neonEffect.setInput(bloom);
        return neonEffect;
    }

    private void handleOfflineMode() {
        String playerName = nameField.getText().trim();
        if (!playerName.isEmpty()) {
            App.iniciarPartidaOffline(playerName);
        } else {
            nameField.setStyle("-fx-border-color: red;");
        }
    }

    private void handleOnlineMode() {
        String playerName = nameField.getText().trim();
        if (!playerName.isEmpty()) {
            App.iniciarPartidaOnline(playerName);
        } else {
            nameField.setStyle("-fx-border-color: red;");
        }
    }

    private void handleHowToPlay() {
        // TODO: Implementar vista de instrucciones
    }

    public Scene getScene() {
        return scene;
    }

    // Getters para los componentes
    public TextField getNameField() {
        return nameField;
    }

    public Button getOnlineButton() {
        return onlineButton;
    }

    public Button getOfflineButton() {
        return offlineButton;
    }

    public Button getHowToPlayButton() {
        return howToPlayButton;
    }

    public Button getExitButton() {
        return exitButton;
    }
}
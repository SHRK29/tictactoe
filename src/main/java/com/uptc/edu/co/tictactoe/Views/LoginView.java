package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import com.uptc.edu.co.tictactoe.Network.ClientConnection;
import com.uptc.edu.co.tictactoe.Network.Request;
import com.uptc.edu.co.tictactoe.Network.Response;

public class LoginView {

    private Scene scene;
    private TextField nameField;
    private Button onlineButton;
    private Button vsPcButton;
    private Button howToPlayButton;
    private Button exitButton;
    private final String SERVER_IP = "localhost"; // Cambia esto según tu configuración
    private final int SERVER_PORT = 8080; // Cambia esto según tu configuración

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

        // Crear botón de historial de puntuaciones mejorado
        Button scoreButton = createScoreButton();

        // Usar StackPane como contenedor raíz para superponer elementos
        StackPane rootLayout = new StackPane();
        rootLayout.getChildren().add(mainLayout);

        // Añadir el botón de score flotante
        StackPane.setAlignment(scoreButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(scoreButton, new Insets(0, 30, 30, 0));
        rootLayout.getChildren().add(scoreButton);

        scene = new Scene(rootLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/login.css").toExternalForm());

        // Configurar acciones de los botones
        configurarAccionesBotones();

        // Configurar acción del botón de historial
        scoreButton.setOnAction(e -> {
            ScoreView scoreView = new ScoreView();
            scoreView.getBackButton().setOnAction(ev -> {
                App.cambiarEscena(this.scene, "Tic Tac Toe - Login");
            });
            App.cambiarEscena(scoreView.getScene(), "Historial de Partidas");
        });
    }

    private Button createScoreButton() {
        ImageView scoreIcon = new ImageView(new Image(getClass().getResourceAsStream("/Icons/ScoreIcon.png")));
        scoreIcon.setFitHeight(185);
        scoreIcon.setFitWidth(185);

        Button scoreButton = new Button();
        scoreButton.setGraphic(scoreIcon);
        scoreButton.getStyleClass().add("score-button");
        scoreButton.setPadding(new Insets(5));
        scoreButton.setBackground(Background.EMPTY);

        return scoreButton;
    }

    private void configurarAccionesBotones() {
        howToPlayButton.setOnAction(e -> {
            HowToPlayView howToPlayView = new HowToPlayView();
            App.cambiarEscena(howToPlayView.getScene(), "Cómo Jugar - Tic Tac Toe");
        });

        vsPcButton.setOnAction(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "Jugador 1";
            }
            GameViewOffline gameView = new GameViewOffline(playerName);
            App.cambiarEscena(gameView.getScene(), "Tic Tac Toe - Modo Local");
        });

        // En el método configurarAccionesBotones(), reemplazar el action del
        // onlineButton:
        onlineButton.setOnAction(e -> {
            final String playerName = nameField.getText().trim().isEmpty() ? "Jugador Online"
                    : nameField.getText().trim();

            // Mostrar la animación de carga
            LoadingView loadingView = new LoadingView();
            loadingView.show();

            // Iniciar conexión en un hilo separado
            new Thread(() -> {
                try {
                    // 1. Establecer conexión con el servidor
                    Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                    ClientConnection connection = new ClientConnection(socket);

                    // 2. Verificar si la conexión es exitosa
                    if (connection.isConnected()) {
                        // 3. Enviar solicitud de registro con el nombre del jugador
                        Map<String, Object> params = new HashMap<>();
                        params.put("playerName", playerName);
                        Request nameRequest = new Request("REGISTER_PLAYER", params);
                        connection.sendRequest(nameRequest.getType(), nameRequest.getParams());

                        // 4. Recibir respuesta del servidor
                        Response response = connection.receiveResponse();

                        Platform.runLater(() -> {
                            if (response != null && "REGISTRATION_SUCCESS".equals(response.getType())) {
                                // 5. Si el registro es exitoso, iniciar el juego online
                                GameViewOnLine gameView = new GameViewOnLine(playerName, connection);
                                App.cambiarEscena(gameView.getScene(), "Tic Tac Toe - Modo Online");
                            } else {
                                // 6. Manejar errores de registro
                                String errorMessage = "Error al conectar";
                                if (response != null && response.getStringData("error") != null) {
                                    errorMessage = response.getStringData("error");
                                }

                                // Mostrar mensaje de error (puedes implementar un diálogo de error)
                                System.err.println("Error de conexión: " + errorMessage);

                                // Cerrar la conexión fallida
                                connection.close();

                                // Volver a la pantalla de login
                                App.cambiarEscena(scene, "Tic Tac Toe - Login");
                            }
                        });
                    } else {
                        Platform.runLater(() -> {
                            System.err.println("No se pudo establecer conexión con el servidor");
                            App.cambiarEscena(scene, "Tic Tac Toe - Login");
                        });
                    }
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        System.err.println("Error de conexión: " + ex.getMessage());
                        App.cambiarEscena(scene, "Tic Tac Toe - Login");
                    });
                }
            }).start();
        });

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

    // Getters para los componentes
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
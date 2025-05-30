package com.uptc.edu.co.tictactoe.Views;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Network.ClientConnection;
import com.uptc.edu.co.tictactoe.Network.Response;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class GameViewOnLine {
    private Scene scene;
    private GridPane gameBoard;
    private Label statusLabel;
    private Label timerLabel;
    private Label player1Label;
    private Label player2Label;
    private Button[][] buttons;
    private String playerName;
    private String opponentName = "Esperando...";
    private String playerSymbol;
    private boolean isMyTurn;
    private ClientConnection clientConnection;
    private Thread listenerThread;
    private final Gson gson = new Gson();
    private Timeline timer;
    private int timeLeft;

    // Imágenes
    private Image playerIconImage;
    private Image opponentIconImage;
    private Image homeIconImage;
    private Image imageX;
    private Image imageO;

    // Rutas a Imágenes
    private final String PATH_PLAYER_ICON = "/Icons/BluePlayer.png";
    private final String PATH_OPPONENT_ICON = "/Icons/RedPlayer.png";
    private final String PATH_HOME_ICON = "/Icons/Home.png";
    private final String PATH_X_IMAGE = "/Icons/X.png";
    private final String PATH_O_IMAGE = "/Icons/O.png";

    private final double ICON_SIZE_HOME = 240.0;
    private final double CELL_SIZE = 140.0;
    private final double GRID_GAP = 15.0;
    private final double LINE_THICKNESS = 4.0;

    private Pane linePane;
    private Line winningLine;

    private static final Logger LOGGER = Logger.getLogger(GameViewOnLine.class.getName());

    public GameViewOnLine(String playerName, ClientConnection clientConnection) {
        this.playerName = playerName;
        this.clientConnection = clientConnection;
        this.buttons = new Button[3][3];
        loadImages();
        initializeUI();
        startListeningForServerMessages();
    }

    private void loadImages() {
        try {
            playerIconImage = cargarImagenSegura(PATH_PLAYER_ICON);
            opponentIconImage = cargarImagenSegura(PATH_OPPONENT_ICON);
            homeIconImage = cargarImagenSegura(PATH_HOME_ICON);
            imageX = cargarImagenSegura(PATH_X_IMAGE);
            imageO = cargarImagenSegura(PATH_O_IMAGE);
        } catch (Exception e) {
            System.err.println("Error crítico al cargar recursos de imagen: " + e.getMessage());
            Platform.exit();
        }
    }

    private Image cargarImagenSegura(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Recurso no encontrado: " + path);
            }
            return new Image(is);
        } catch (IOException e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar la imagen: " + path, e);
        }
    }

    private void initializeUI() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Panel superior con nombres de jugadores y timer
        HBox playersPanel = new HBox(50);
        playersPanel.setAlignment(Pos.CENTER);

        VBox player1Box = createPlayerBox(playerName, playerIconImage, true);
        VBox player2Box = createPlayerBox(opponentName, opponentIconImage, false);
        
        // Timer en el centro
        timerLabel = new Label("30");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        timerLabel.setTextFill(Color.RED);
        
        playersPanel.getChildren().addAll(player1Box, timerLabel, player2Box);
        
        // Status label
        statusLabel = new Label("Esperando oponente...");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Game board
        gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(GRID_GAP);
        gameBoard.setVgap(GRID_GAP);
        
        initializeGameBoard();
        
        root.getChildren().addAll(playersPanel, statusLabel, gameBoard);
        scene = new Scene(root, 800, 600);
    }

    private VBox createPlayerBox(String name, Image icon, boolean isPlayer1) {
        VBox playerBox = new VBox(10);
        playerBox.setAlignment(Pos.CENTER);
        
        ImageView playerIcon = new ImageView(icon);
        playerIcon.setFitHeight(50);
        playerIcon.setFitWidth(50);
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        if (isPlayer1) {
            player1Label = nameLabel;
        } else {
            player2Label = nameLabel;
        }
        
        playerBox.getChildren().addAll(playerIcon, nameLabel);
        return playerBox;
    }

    private void initializeGameBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = createGameButton();
                buttons[i][j] = button;
                gameBoard.add(button, j, i);
                
                final int row = i;
                final int col = j;
                button.setOnAction(e -> handleButtonClick(row, col));
            }
        }
    }

    private Button createGameButton() {
        Button button = new Button();
        button.setPrefSize(CELL_SIZE, CELL_SIZE);
        button.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");
        button.setDisable(true);
        return button;
    }

    private void handleButtonClick(int row, int col) {
        if (!isMyTurn || buttons[row][col].getGraphic() != null) {
            return;
        }

        try {
            Map<String, Object> moveData = new HashMap<>();
            moveData.put("row", String.valueOf(row));
            moveData.put("col", String.valueOf(col));
            clientConnection.sendRequest("move", moveData);
            
            updateButton(row, col, playerSymbol);
            stopTimer();
        } catch (IOException e) {
            LOGGER.severe("Error al enviar movimiento: " + e.getMessage());
            // Mostrar mensaje de error al usuario
            Platform.runLater(() -> statusLabel.setText("Error de conexión"));
        }
    }

    private void updateButton(int row, int col, String symbol) {
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(symbol.equals("X") ? imageX : imageO);
            imageView.setFitHeight(CELL_SIZE * 0.8);
            imageView.setFitWidth(CELL_SIZE * 0.8);
            buttons[row][col].setGraphic(imageView);
        });
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        
        timeLeft = 30;
        updateTimerLabel();
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            updateTimerLabel();
            if (timeLeft <= 0) {
                timer.stop();
                if (isMyTurn) {
                    // Enviar movimiento aleatorio si se acaba el tiempo
                    makeRandomMove();
                }
            }
        }));
        timer.setCycleCount(30);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTimerLabel() {
        Platform.runLater(() -> timerLabel.setText(String.valueOf(timeLeft)));
    }

    private void makeRandomMove() {
        // Buscar una casilla vacía aleatoria
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getGraphic() == null) {
                    handleButtonClick(i, j);
                    return;
                }
            }
        }
    }

    private void startListeningForServerMessages() {
        listenerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Response response = clientConnection.receiveResponse();
                    Platform.runLater(() -> handleServerResponse(response));
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    System.err.println("Error en la conexión: " + e.getMessage());
                    handleConnectionError();
                });
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleConnectionError() {
        ConnectionErrorView errorView = new ConnectionErrorView();
        errorView.show();
    }

    private void handleServerResponse(Response response) {
        if (response == null || response.getType() == null) {
            System.err.println("Respuesta inválida del servidor");
            return;
        }

        try {
            switch (response.getType()) {
                case "login_success":
                case "loginConfirm":
                    handleLoginConfirm(response.getData());
                    break;
                case "waiting":
                    handleWaiting(response.getData());
                    break;
                case "gameStart":
                    handleGameStart(response.getData());
                    break;
                case "yourTurn":
                    handleYourTurn(response.getData());
                    break;
                case "opponentTurn":
                    handleOpponentTurn(response.getData());
                    break;
                case "gameState":
                    handleGameState(response.getData());
                    break;
                case "moveError":
                    handleMoveError(response.getData());
                    break;
                case "gameOver":
                    handleGameOver(response.getData());
                    break;
                case "opponentDisconnected":
                    handleOpponentDisconnected(response.getData());
                    break;
                case "error":
                    handleError(response.getData());
                    break;
                case "warning":
                    handleWarning(response.getData());
                    break;
                case "autoMove":
                    handleAutoMove(response.getData());
                    break;
                case "timeoutMove":
                    handleTimeoutMove(response.getData());
                    break;
                default:
                    LOGGER.warning("Tipo de respuesta desconocido: " + response.getType());
            }
        } catch (Exception e) {
            LOGGER.severe("Error procesando respuesta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleWaiting(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText("Esperando a otro jugador para iniciar la partida...");
            setAllButtonsDisabled(true);
            
            // Actualizar el nombre del jugador actual si está disponible
            if (data != null && data.containsKey("playerName")) {
                playerName = data.get("playerName").toString();
                Label player1Label = (Label) scene.lookup(".player-name");
                if (player1Label != null) {
                    player1Label.setText(playerName.toUpperCase());
                }
            }
        });
    }

    private void handleLoginConfirm(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText("Conectado al servidor. Esperando oponente...");
            // Enviar solicitud de partida
            try {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("playerName", playerName);
                clientConnection.sendRequest("requestMatch", requestData);
            } catch (IOException e) {
                System.err.println("Error al solicitar partida: " + e.getMessage());
                handleConnectionError();
            }
        });
    }

    private void handleGameStart(Map<String, Object> data) {
        Platform.runLater(() -> {
            LOGGER.info("Handling gameStart: " + data);
            // Obtener los datos del juego
            String player1NameFromServer = (String) data.get("player1");
            String player2NameFromServer = (String) data.get("player2");
            String symbol1 = (String) data.get("symbol1"); // Typically "X"
            String symbol2 = (String) data.get("symbol2"); // Typically "O"
            
            // Determinar si somos el jugador 1 o 2
            // Ensure playerName is not null and matches one of the server player names
            if (this.playerName == null) {
                LOGGER.severe("Error: playerName is null in handleGameStart");
                statusLabel.setText("Error de configuración del jugador.");
                return;
            }

            boolean isPlayer1 = this.playerName.equals(player1NameFromServer);
            boolean isPlayer2 = this.playerName.equals(player2NameFromServer);

            if (isPlayer1) {
                this.playerSymbol = symbol1;
                this.opponentName = player2NameFromServer;
                this.isMyTurn = true; // Player 1 (X) usually starts
                 LOGGER.info("Client is Player 1 (" + this.playerSymbol + "). Opponent: " + this.opponentName);
            } else if (isPlayer2) {
                this.playerSymbol = symbol2;
                this.opponentName = player1NameFromServer;
                this.isMyTurn = false; // Player 2 (O) waits for Player 1's move
                LOGGER.info("Client is Player 2 (" + this.playerSymbol + "). Opponent: " + this.opponentName);
            } else {
                LOGGER.severe("Error: Client playerName (" + this.playerName + ") does not match player1 (" + player1NameFromServer + ") or player2 (" + player2NameFromServer + ") from server.");
                statusLabel.setText("Error al unirse a la partida.");
                // Optionally, prevent further game actions if roles can't be assigned
                setAllButtonsDisabled(true);
                return;
            }
            
            // Actualizar la interfaz
            // Ensure player1Label and player2Label are initialized
            if (player1Label == null || player2Label == null) {
                 LOGGER.severe("Error: Player labels not initialized in handleGameStart.");
                 return;
            }
            
            if (isPlayer1) {
                player1Label.setText(this.playerName + " (" + this.playerSymbol + ")");
                player2Label.setText(this.opponentName + " (" + symbol2 + ")");
            } else {
                player1Label.setText(this.opponentName + " (" + symbol1 + ")");
                player2Label.setText(this.playerName + " (" + this.playerSymbol + ")");
            }
            
            statusLabel.setText(this.isMyTurn ? "Tu turno" : "Turno de " + this.opponentName);
            
            // Habilitar/deshabilitar botones según el turno
            setAllButtonsDisabled(!this.isMyTurn);
            
            // Iniciar el timer si es nuestro turno
            if (this.isMyTurn) {
                startTimer();
            }
        });
    }

    private void handleYourTurn(Map<String, Object> data) {
        if (data == null) {
            LOGGER.warning("Datos de turno nulos");
            return;
        }
        LOGGER.info("Handling yourTurn: " + data);

        try {
            this.isMyTurn = true;
            // String currentPlayerName = (String) data.get("currentPlayer"); // Redundant if we know it's our turn
            // String currentSymbol = (String) data.get("symbol"); // This should be our playerSymbol

            Platform.runLater(() -> {
                statusLabel.setText("Tu turno (" + this.playerSymbol + ")");
                setAllButtonsDisabled(false); // Enable buttons for player's move
                startTimer();
            });
        } catch (Exception e) {
            LOGGER.severe("Error al procesar tu turno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleOpponentTurn(Map<String, Object> data) {
        if (data == null) {
            LOGGER.warning("Datos de turno del oponente nulos");
            return;
        }
        LOGGER.info("Handling opponentTurn: " + data);
        
        try {
            this.isMyTurn = false;
            // String opponentPlayerName = (String) data.get("currentPlayer"); // This is the opponent's name

            Platform.runLater(() -> {
                statusLabel.setText("Turno de " + this.opponentName);
                setAllButtonsDisabled(true); // Disable buttons as it's opponent's turn
                stopTimer();
            });
        } catch (Exception e) {
            LOGGER.severe("Error al procesar turno del oponente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameState(Map<String, Object> data) {
        if (data == null) {
            LOGGER.warning("Datos de estado del juego nulos");
            return;
        }
        LOGGER.info("Handling gameState: " + data);

        try {
            String boardJson = (String) data.get("board");
            String lastMove = (String) data.get("lastMove"); // e.g., "0,1"
            // String currentSymbolMakingMove = (String) data.get("symbol"); // Symbol that made the last move
            // String nextPlayerName = (String) data.get("currentPlayer"); // Player whose turn it is next (not needed if using yourTurn/opponentTurn)


            Platform.runLater(() -> {
                // Actualizar el tablero
                Type boardType = new TypeToken<String[][]>(){}.getType();
                String[][] boardArray = gson.fromJson(boardJson, boardType);
                
                if (boardArray == null) {
                    LOGGER.severe("Error: Board array is null after parsing from JSON: " + boardJson);
                    return;
                }

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (boardArray[i][j] != null && !boardArray[i][j].isEmpty()) {
                            updateButtonGraphic(i, j, boardArray[i][j]);
                        }
                    }
                }

                // Si se especificó el último movimiento, resaltarlo (opcional)
                if (lastMove != null) {
                    try {
                        String[] coords = lastMove.split(",");
                        if (coords.length == 2) {
                            int row = Integer.parseInt(coords[0]);
                            int col = Integer.parseInt(coords[1]);
                            // highlightButton(row, col); // Assuming highlightButton is defined
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Error parsing lastMove coordinates: " + lastMove + " - " + e.getMessage());
                    }
                }
                
                // No cambiar el turno aquí, esperar a yourTurn/opponentTurn
                // La lógica de turnos y habilitación de botones se maneja en handleYourTurn/handleOpponentTurn
                // statusLabel.setText(isMyTurn ? "Tu turno" : "Turno de " + opponentName);
                // setAllButtonsDisabled(!isMyTurn);
            });
        } catch (Exception e) {
            LOGGER.severe("Error al procesar estado del juego: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed debugging
        }
    }

    private void highlightButton(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            Button button = buttons[row][col];
            // Agregar efecto de resaltado
            button.setStyle(button.getStyle() + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,255,0.8), 10, 0, 0, 0);");
            
            // Remover el resaltado después de un segundo
            Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> button.setStyle(button.getStyle().replace("; -fx-effect: dropshadow(three-pass-box, rgba(0,0,255,0.8), 10, 0, 0, 0);", ""))
            ));
            timeline.play();
        }
    }

    private void updateBoard(Map<String, String> board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String key = i + "," + j;
                String value = board.get(key);
                if (value != null && !value.isEmpty()) {
                    updateButtonGraphic(i, j, value);
                }
            }
        }
    }

    private void updateButtonGraphic(int row, int col, String symbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || symbol == null) {
            LOGGER.warning("Intento de actualizar botón con parámetros inválidos: row=" + row + ", col=" + col + ", symbol=" + symbol);
            return;
        }
        
        Platform.runLater(() -> {
            try {
                Button button = buttons[row][col];
                if (button != null) {
                    Image image = symbol.equals("X") ? imageX : imageO;
                    if (image != null) {
                        ImageView imageView = createImageView(image, CELL_SIZE * 0.8);
                        imageView.setEffect(createNeonEffect(symbol.equals("X") ? 
                            Color.rgb(0, 255, 238) : Color.rgb(255, 45, 241)));
                        button.setGraphic(imageView);
                        button.setDisable(true);
                    } else {
                        LOGGER.warning("Imagen nula para el símbolo: " + symbol);
                    }
                } else {
                    LOGGER.warning("Botón nulo en posición: " + row + "," + col);
                }
            } catch (Exception e) {
                LOGGER.severe("Error actualizando gráfico del botón: " + e.getMessage());
            }
        });
    }

    private void handleMoveError(Map<String, Object> data) {
        Platform.runLater(() -> {
            String message = data != null && data.containsKey("message") ? 
                String.valueOf(data.get("message")) : "Error al realizar el movimiento";
            statusLabel.setText("Error: " + message);
            // Reactivar los botones
            setAllButtonsDisabled(false);
        });
    }

    private void handleGameOver(Map<String, Object> data) {
        Platform.runLater(() -> {
            String winner = data.get("winner") != null ? data.get("winner").toString() : null;
            if (winner != null) {
                statusLabel.setText(winner.equals(playerName) ? 
                    "¡Has ganado! - Jugaste con " + playerSymbol : 
                    "¡Ha ganado " + winner + "! - Jugaste con " + playerSymbol);
            } else if ("draw".equals(data.get("result"))) {
                statusLabel.setText("¡Empate! - Jugaste con " + playerSymbol);
            }
            setAllButtonsDisabled(true);
            
            // Actualizar el tablero final si está disponible
            @SuppressWarnings("unchecked")
            Map<String, String> finalBoard = gson.fromJson((String)data.get("board"), Map.class);
            if (finalBoard != null) {
                updateBoard(finalBoard);
            }
            stopTimer();
        });
    }

    private void handleOpponentDisconnected(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText(data.get("message").toString());
            // Deshabilitar todos los botones
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setDisable(true);
                }
            }
            stopTimer();
        });
    }

    private void handleError(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText("Error: " + data.get("message").toString());
        });
    }

    private void handleMove(int row, int col) {
        if (!isMyTurn) {
            LOGGER.warning("Intento de movimiento fuera de turno");
            return;
        }

        try {
            Map<String, Object> moveData = new HashMap<>();
            moveData.put("row", String.valueOf(row));
            moveData.put("col", String.valueOf(col));
            
            // Deshabilitar todos los botones mientras esperamos respuesta
            setAllButtonsDisabled(true);
            
            // Enviar movimiento al servidor
            clientConnection.sendRequest("move", moveData);
            
            // Actualizar estado local
            statusLabel.setText("Esperando respuesta del oponente...");
        } catch (Exception e) {
            LOGGER.severe("Error al enviar movimiento: " + e.getMessage());
            statusLabel.setText("Error al realizar el movimiento");
            setAllButtonsDisabled(false);
        }
    }

    private void updateButtonsState() {
        Platform.runLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getGraphic() == null) {
                        buttons[i][j].setDisable(!isMyTurn);
                    }
                }
            }
        });
    }

    private void setAllButtonsDisabled(boolean disabled) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getGraphic() == null) {
                    buttons[i][j].setDisable(disabled);
                }
            }
        }
    }

    private void updateStatus() {
        Platform.runLater(() -> {
            if (opponentName.equals("Esperando...")) {
                statusLabel.setText("Esperando a otro jugador...");
            } else {
                statusLabel.setText(isMyTurn ? "Tu turno" : "Turno de " + opponentName);
            }
        });
    }

    private void handleExit() {
        try {
            Map<String, Object> exitData = new HashMap<>();
            exitData.put("playerName", playerName);
            clientConnection.sendRequest("exit", exitData);
        } catch (Exception e) {
            System.err.println("Error enviando solicitud de salida: " + e.getMessage());
        } finally {
            if (listenerThread != null) {
                listenerThread.interrupt();
            }
            clientConnection.close();
            App.mostrarLoginView();
        }
    }

    private void handleWarning(Map<String, Object> data) {
        if (data != null && data.containsKey("message")) {
            Platform.runLater(() -> {
                statusLabel.setText(String.valueOf(data.get("message")));
            });
        }
    }

    private void handleAutoMove(Map<String, Object> data) {
        if (data != null && data.containsKey("message")) {
            Platform.runLater(() -> {
                statusLabel.setText(String.valueOf(data.get("message")));
            });
        }
    }

    private void handleTimeoutMove(Map<String, Object> data) {
        if (data != null) {
            Platform.runLater(() -> {
                String message = (String) data.get("message");
                statusLabel.setText(message);
                
                // Actualizar el tablero si se proporciona
                String boardJson = (String) data.get("board");
                if (boardJson != null) {
                    Type boardType = new TypeToken<String[][]>(){}.getType();
                    String[][] board = gson.fromJson(boardJson, boardType);
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (board[i][j] != null && !board[i][j].isEmpty()) {
                                updateButtonGraphic(i, j, board[i][j]);
                            }
                        }
                    }
                }
            });
        }
    }

    private ImageView createImageView(Image image, double fitSize) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitSize);
        imageView.setFitHeight(fitSize);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private Effect createNeonEffect(Color color) {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(color);
        neonEffect.setSpread(0.5);
        neonEffect.setRadius(20);
        Bloom bloom = new Bloom(0.3);
        neonEffect.setInput(bloom);
        return neonEffect;
    }

    public Scene getScene() {
        return scene;
    }
}

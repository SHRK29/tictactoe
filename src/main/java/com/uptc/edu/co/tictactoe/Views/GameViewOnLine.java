package com.uptc.edu.co.tictactoe.Views;

import com.google.gson.Gson;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GameViewOnLine {
    private Scene scene;
    private GridPane gameBoard;
    private Label statusLabel;
    private Button[][] buttons;
    private String playerName;
    private String opponentName = "Esperando...";
    private String playerSymbol;
    private boolean isMyTurn;
    private ClientConnection clientConnection;
    private Thread listenerThread;
    private final Gson gson = new Gson();

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
        BorderPane root = new BorderPane();
        root.getStyleClass().add("game-background");
        root.setPadding(new Insets(20));

        // Header
        root.setTop(createHeader());
        // Centro del juego
        root.setCenter(createGameCenter());
        // Footer
        root.setBottom(createFooter());

        scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/game.css").toExternalForm());
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.getStyleClass().add("header-container");
        headerBox.setMaxWidth(Double.MAX_VALUE);

        // Jugador 1
        HBox player1Box = new HBox(20);
        player1Box.getStyleClass().add("player-box");
        ImageView player1Icon = createImageView(playerIconImage, 240);
        player1Icon.getStyleClass().add("player-icon");
        Label player1Label = new Label(playerName.toUpperCase());
        player1Label.getStyleClass().add("player-name");
        player1Box.getChildren().addAll(player1Icon, player1Label);

        // VS
        Label vsLabel = new Label("VS");
        vsLabel.getStyleClass().add("vs-container");

        // Oponente
        HBox player2Box = new HBox(20);
        player2Box.getStyleClass().add("player-box");
        Label player2Label = new Label(opponentName);
        player2Label.getStyleClass().add("player-name");
        player2Label.setId("player2Name");
        ImageView player2Icon = createImageView(opponentIconImage, 240);
        player2Icon.getStyleClass().add("player-icon");
        player2Box.getChildren().addAll(player2Label, player2Icon);

        // Espaciadores
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(
                leftSpacer,
                player1Box,
                vsLabel,
                player2Box,
                rightSpacer);

        return headerBox;
    }

    private HBox createGameCenter() {
        HBox centerContainer = new HBox(40);
        centerContainer.setAlignment(Pos.CENTER);

        // Logo O
        ImageView oLogo = createImageView(imageO, 250);
        oLogo.setEffect(createNeonEffect(Color.rgb(255, 45, 241)));

        // Tablero
        linePane = createLinePane();
        gameBoard = createGameBoard();
        StackPane boardPane = new StackPane();
        boardPane.getStyleClass().add("board-container");
        boardPane.setAlignment(Pos.CENTER);
        boardPane.getChildren().addAll(linePane, gameBoard);

        // Logo X
        ImageView xLogo = createImageView(imageX, 250);
        xLogo.setEffect(createNeonEffect(Color.rgb(0, 255, 238)));

        centerContainer.getChildren().addAll(oLogo, boardPane, xLogo);
        return centerContainer;
    }

    private HBox createFooter() {
        HBox footerBox = new HBox(20);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(20, 0, 0, 0));

        // Estado del juego
        statusLabel = new Label("Esperando al otro jugador...");
        statusLabel.getStyleClass().add("status-label");

        // Botón de salida
        Button exitButton = new Button();
        exitButton.getStyleClass().add("home-button");
        ImageView homeIcon = createImageView(homeIconImage, ICON_SIZE_HOME);
        exitButton.setGraphic(homeIcon);
        exitButton.setOnAction(e -> handleExit());

        footerBox.getChildren().addAll(statusLabel, exitButton);
        return footerBox;
    }

    private GridPane createGameBoard() {
        GridPane grid = new GridPane();
        grid.setHgap(GRID_GAP);
        grid.setVgap(GRID_GAP);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.getStyleClass().add("game-button");
                button.setPrefSize(CELL_SIZE, CELL_SIZE);
                final int row = i;
                final int col = j;
                button.setOnAction(e -> handleMove(row, col));
                buttons[i][j] = button;
                grid.add(button, j, i);
            }
        }
        return grid;
    }

    private Pane createLinePane() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        pane.setPrefSize(3 * CELL_SIZE + 2 * GRID_GAP, 3 * CELL_SIZE + 2 * GRID_GAP);
        return pane;
    }

    private ImageView createImageView(Image image, double fitSize) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitSize);
        imageView.setFitHeight(fitSize);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(color);
        neonEffect.setSpread(0.5);
        neonEffect.setRadius(20);
        Bloom bloom = new Bloom(0.3);
        neonEffect.setInput(bloom);
        return neonEffect;
    }

    private void drawWinningLine(int startRow, int startCol, int endRow, int endCol) {
        double startX = startCol * (CELL_SIZE + GRID_GAP) + CELL_SIZE / 2;
        double startY = startRow * (CELL_SIZE + GRID_GAP) + CELL_SIZE / 2;
        double endX = endCol * (CELL_SIZE + GRID_GAP) + CELL_SIZE / 2;
        double endY = endRow * (CELL_SIZE + GRID_GAP) + CELL_SIZE / 2;

        winningLine = new Line(startX, startY, endX, endY);
        winningLine.setStroke(Color.rgb(246, 220, 67));
        winningLine.setStrokeWidth(LINE_THICKNESS);
        winningLine.setStrokeLineCap(StrokeLineCap.ROUND);
        winningLine.setEffect(createNeonEffect(Color.rgb(246, 220, 67)));

        linePane.getChildren().add(winningLine);
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
                case "loginConfirm":
                    handleLoginConfirm(response.getData());
                    break;
                case "gameStart":
                    handleGameStart(response.getData());
                    break;
                case "waitingForOpponent":
                    handleWaitingForOpponent(response.getData());
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
                default:
                    System.err.println("Tipo de respuesta desconocido: " + response.getType());
            }
        } catch (Exception e) {
            System.err.println("Error procesando respuesta: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
            playerSymbol = data.get("symbol").toString();
            opponentName = data.get("opponentName").toString();
            isMyTurn = playerSymbol.equals("X"); // X siempre empieza
            
            Platform.runLater(() -> {
                Label player2Label = (Label) scene.lookup("#player2Name");
                if (player2Label != null) {
                    player2Label.setText(opponentName.toUpperCase());
                }
                updateStatus();
                updateButtonsState();
            });
        } catch (Exception e) {
            System.err.println("Error al iniciar el juego: " + e.getMessage());
        }
    }

    private void handleWaitingForOpponent(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText(data.get("message").toString());
        });
    }

    private void handleGameState(Map<String, Object> data) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> board = (Map<String, Object>) data.get("board");
            Boolean isPlayerTurn = (Boolean) data.get("isYourTurn");
            String lastMove = (String) data.get("lastMove");

            if (isPlayerTurn != null) {
                this.isMyTurn = isPlayerTurn;
            }

            if (board != null) {
                updateBoard(board);
            }

            if (lastMove != null) {
                String[] coords = lastMove.split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                updateButtonGraphic(row, col, !isMyTurn ? playerSymbol : (playerSymbol.equals("X") ? "O" : "X"));
            }

            updateStatus();
            updateButtonsState();
        } catch (Exception e) {
            System.err.println("Error actualizando estado del juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBoard(Map<String, Object> board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String key = i + "," + j;
                String value = (String) board.get(key);
                if (value != null && !value.isEmpty()) {
                    updateButtonGraphic(i, j, value);
                }
            }
        }
    }

    private void updateButtonGraphic(int row, int col, String symbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3) return;
        
        Platform.runLater(() -> {
            Button button = buttons[row][col];
            if (button != null) {
                Image image = symbol.equals("X") ? imageX : imageO;
                ImageView imageView = createImageView(image, CELL_SIZE * 0.8);
                imageView.setEffect(createNeonEffect(symbol.equals("X") ? 
                    Color.rgb(0, 255, 238) : Color.rgb(255, 45, 241)));
                button.setGraphic(imageView);
                button.setDisable(true);
            }
        });
    }

    private void handleMoveError(Map<String, Object> data) {
        Platform.runLater(() -> {
            String message = data.get("message").toString();
            statusLabel.setText("Error: " + message);
            // Reactivar los botones
            setAllButtonsDisabled(false);
        });
    }

    private void handleGameOver(Map<String, Object> data) {
        Platform.runLater(() -> {
            String winner = data.get("winner") != null ? data.get("winner").toString() : null;
            if (winner != null) {
                statusLabel.setText(winner.equals(playerName) ? "¡Has ganado!" : "¡Ha ganado " + winner + "!");
            } else if ("draw".equals(data.get("result"))) {
                statusLabel.setText("¡Empate!");
            }
            setAllButtonsDisabled(true);
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
        });
    }

    private void handleError(Map<String, Object> data) {
        Platform.runLater(() -> {
            statusLabel.setText("Error: " + data.get("message").toString());
        });
    }

    private void handleMove(int row, int col) {
        if (!isMyTurn) {
            System.out.println("No es tu turno");
            return;
        }

        Map<String, Object> moveData = new HashMap<>();
        moveData.put("row", row);
        moveData.put("col", col);
        moveData.put("symbol", playerSymbol);

        try {
            clientConnection.sendRequest("move", moveData);
            isMyTurn = false;
            updateStatus();
            updateButtonsState();
        } catch (Exception e) {
            System.err.println("Error enviando movimiento: " + e.getMessage());
            handleConnectionError();
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

    public Scene getScene() {
        return scene;
    }
}

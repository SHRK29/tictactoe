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
    private BorderPane rootLayout;
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
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("game-background");
        rootLayout.setPadding(new Insets(20));

        HBox header = createHeader();
        rootLayout.setTop(header);

        VBox centerGameArea = createCenterGameArea();
        rootLayout.setCenter(centerGameArea);
        
        HBox footer = createFooter();
        rootLayout.setBottom(footer);

        scene = new Scene(rootLayout, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/game.css").toExternalForm());
    }

    private HBox createHeader() {
        HBox headerBox = new HBox();
        headerBox.getStyleClass().add("header-container");

        VBox player1VBox = new VBox(10);
        player1VBox.setAlignment(Pos.CENTER);
        ImageView player1Icon = new ImageView(playerIconImage);
        player1Icon.setFitHeight(50);
        player1Icon.setFitWidth(50);
        player1Icon.getStyleClass().add("player-icon");
        player1Label = new Label(playerName);
        player1Label.getStyleClass().add("player-name");
        player1VBox.getChildren().addAll(player1Icon, player1Label);

        timerLabel = new Label("30");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        timerLabel.setTextFill(Color.RED);

        VBox player2VBox = new VBox(10);
        player2VBox.setAlignment(Pos.CENTER);
        ImageView player2Icon = new ImageView(opponentIconImage);
        player2Icon.setFitHeight(50);
        player2Icon.setFitWidth(50);
        player2Icon.getStyleClass().add("player-icon");
        player2Label = new Label(opponentName);
        player2Label.getStyleClass().add("player-name");
        player2VBox.getChildren().addAll(player2Icon, player2Label);
        
        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(leftSpacer, player1VBox, timerLabel, player2VBox, rightSpacer);
        return headerBox;
    }

    private VBox createCenterGameArea() {
        VBox centerArea = new VBox(20);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setPadding(new Insets(20));

        statusLabel = new Label("Esperando oponente...");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        gameBoard = new GridPane();
        gameBoard.setAlignment(Pos.CENTER);
        gameBoard.setHgap(GRID_GAP);
        gameBoard.setVgap(GRID_GAP);
        gameBoard.getStyleClass().add("game-grid");

        initializeGameBoard();

        linePane = new Pane();
        linePane.getStyleClass().add("line-pane");
        linePane.setPickOnBounds(false);
        
        // Calcular dimensiones del tablero para las líneas
        double boardTotalWidth = 3 * CELL_SIZE + 2 * GRID_GAP;
        double boardTotalHeight = 3 * CELL_SIZE + 2 * GRID_GAP;

        // Crear y añadir las líneas de la grilla
        // Línea Vertical 1
        Line vLine1 = new Line(CELL_SIZE + GRID_GAP / 2, 0,
                               CELL_SIZE + GRID_GAP / 2, boardTotalHeight);
        vLine1.setStroke(Color.YELLOW);
        vLine1.setStrokeWidth(LINE_THICKNESS);
        vLine1.setStrokeLineCap(StrokeLineCap.ROUND);
        vLine1.setEffect(createNeonEffect(Color.YELLOW));

        // Línea Vertical 2
        Line vLine2 = new Line(2 * CELL_SIZE + GRID_GAP + GRID_GAP / 2, 0,
                               2 * CELL_SIZE + GRID_GAP + GRID_GAP / 2, boardTotalHeight);
        vLine2.setStroke(Color.YELLOW);
        vLine2.setStrokeWidth(LINE_THICKNESS);
        vLine2.setStrokeLineCap(StrokeLineCap.ROUND);
        vLine2.setEffect(createNeonEffect(Color.YELLOW));

        // Línea Horizontal 1
        Line hLine1 = new Line(0, CELL_SIZE + GRID_GAP / 2,
                               boardTotalWidth, CELL_SIZE + GRID_GAP / 2);
        hLine1.setStroke(Color.YELLOW);
        hLine1.setStrokeWidth(LINE_THICKNESS);
        hLine1.setStrokeLineCap(StrokeLineCap.ROUND);
        hLine1.setEffect(createNeonEffect(Color.YELLOW));

        // Línea Horizontal 2
        Line hLine2 = new Line(0, 2 * CELL_SIZE + GRID_GAP + GRID_GAP / 2,
                               boardTotalWidth, 2 * CELL_SIZE + GRID_GAP + GRID_GAP / 2);
        hLine2.setStroke(Color.YELLOW);
        hLine2.setStrokeWidth(LINE_THICKNESS);
        hLine2.setStrokeLineCap(StrokeLineCap.ROUND);
        hLine2.setEffect(createNeonEffect(Color.YELLOW));

        linePane.getChildren().addAll(vLine1, vLine2, hLine1, hLine2);
        
        StackPane boardStackPane = new StackPane();
        boardStackPane.getStyleClass().add("board-container");
        boardStackPane.setAlignment(Pos.CENTER);
        boardStackPane.getChildren().addAll(gameBoard, linePane);

        ImageView oLogo = createImageView(imageO, 150);
        oLogo.setEffect(createNeonEffect(Color.rgb(255, 45, 241)));

        ImageView xLogo = createImageView(imageX, 150);
        xLogo.setEffect(createNeonEffect(Color.rgb(0, 255, 238)));

        HBox boardWithLogos = new HBox(30);
        boardWithLogos.setAlignment(Pos.CENTER);
        boardWithLogos.getChildren().addAll(oLogo, boardStackPane, xLogo);
        
        centerArea.getChildren().addAll(statusLabel, boardWithLogos);
        return centerArea;
    }
    
    private HBox createFooter() {
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.setPadding(new Insets(20, 50, 20, 50));
        footerBox.setMaxWidth(Double.MAX_VALUE);
        footerBox.setSpacing(20);

        Label titleLabel = new Label("TIC TAC TOE ONLINE");
        titleLabel.getStyleClass().add("main-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = new Button();
        ImageView homeIcon = createImageView(homeIconImage, ICON_SIZE_HOME / 1.5);
        homeButton.setGraphic(homeIcon);
        homeButton.getStyleClass().add("home-button");
        homeButton.setOnAction(e -> {
            handleExit();
        });

        footerBox.getChildren().addAll(titleLabel, spacer, homeButton);
        return footerBox;
    }

    private ImageView createImageView(Image image, double fitSize) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(fitSize);
        imageView.setFitHeight(fitSize);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");
        return imageView;
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(color);
        neonEffect.setRadius(20);
        neonEffect.setSpread(0.5);
        Bloom bloom = new Bloom(0.3);
        neonEffect.setInput(bloom);
        return neonEffect;
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
        button.getStyleClass().add("grid-cell");
        button.setDisable(true);
        return button;
    }

    private void handleButtonClick(int row, int col) {
        LOGGER.info("[handleButtonClick] Attempting click on (" + row + "," + col + "). isMyTurn: " + isMyTurn);
        Button clickedButton = buttons[row][col];
        if (clickedButton == null) {
            LOGGER.severe("[handleButtonClick] Button at (" + row + "," + col + ") is NULL!");
            return;
        }

        if (!isMyTurn || clickedButton.getGraphic() != null) {
            if (!isMyTurn) {
                LOGGER.warning("[handleButtonClick] Not my turn.");
            }
            if (clickedButton.getGraphic() != null) {
                LOGGER.warning("[handleButtonClick] Button (" + row + "," + col + ") already has a graphic.");
            }
            return;
        }

        LOGGER.info("[handleButtonClick] Proceeding with move for (" + row + "," + col + ")");
        try {
            Map<String, Object> moveData = new HashMap<>();
            moveData.put("row", String.valueOf(row));
            moveData.put("col", String.valueOf(col));
            clientConnection.sendRequest("move", moveData);
            
            updateButtonGraphic(row, col, playerSymbol);
            stopTimer();
        } catch (IOException e) {
            LOGGER.severe("Error al enviar movimiento: " + e.getMessage());
            Platform.runLater(() -> statusLabel.setText("Error de conexión"));
        }
    }

    private void updateButtonGraphic(int row, int col, String symbol) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || symbol == null) {
            LOGGER.warning("[updateButtonGraphic] Invalid parameters: row=" + row + ", col=" + col + ", symbol=" + symbol);
            return;
        }

        Platform.runLater(() -> {
            try {
                Button button = buttons[row][col];
                if (button != null) {
                    button.setGraphic(null); 
                    Image imageToSet = symbol.equals("X") ? imageX : imageO;
                    
                    if (imageToSet != null) {
                        ImageView imageView = createImageView(imageToSet, CELL_SIZE * 0.8);
                        button.setGraphic(imageView);
                        button.setDisable(true);
                        LOGGER.info("[updateButtonGraphic] Successfully set graphic " + symbol + " on button (" + row + "," + col + ")");
                    } else {
                        LOGGER.warning("[updateButtonGraphic] imageToSet is NULL for symbol: " + symbol + " at (" + row + "," + col + ")");
                    }
                } else {
                    LOGGER.warning("[updateButtonGraphic] Button is NULL at position: (" + row + "," + col + ")");
                }
            } catch (Exception e) {
                LOGGER.severe("[updateButtonGraphic] Exception while updating button graphic at (" + row + "," + col + "): " + e.getMessage());
                e.printStackTrace();
            }
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
            String player1NameFromServer = (String) data.get("player1");
            String player2NameFromServer = (String) data.get("player2");
            String symbol1 = (String) data.get("symbol1");
            String symbol2 = (String) data.get("symbol2");
            
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
                this.isMyTurn = true;
                LOGGER.info("Client is Player 1 (" + this.playerSymbol + "). Opponent: " + this.opponentName);
            } else if (isPlayer2) {
                this.playerSymbol = symbol2;
                this.opponentName = player1NameFromServer;
                this.isMyTurn = false;
                LOGGER.info("Client is Player 2 (" + this.playerSymbol + "). Opponent: " + this.opponentName);
            } else {
                LOGGER.severe("Error: Client playerName (" + this.playerName + ") does not match player1 (" + player1NameFromServer + ") or player2 (" + player2NameFromServer + ") from server.");
                statusLabel.setText("Error al unirse a la partida.");
                setAllButtonsDisabled(true);
                return;
            }
            
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
            setAllButtonsDisabled(!this.isMyTurn);
            if (this.isMyTurn) {
                setAllButtonsDisabled(false);
                LOGGER.info("[handleGameStart] Botones habilitados para el turno del jugador");
                startTimer();
            } else {
                setAllButtonsDisabled(true);
                LOGGER.info("[handleGameStart] Botones deshabilitados, turno del oponente.");
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
            Platform.runLater(() -> {
                statusLabel.setText("Tu turno (" + this.playerSymbol + ")");
                setAllButtonsDisabled(false);
                LOGGER.info("[handleYourTurn] Botones habilitados para el turno del jugador");
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

            Platform.runLater(() -> {
                statusLabel.setText("Turno de " + this.opponentName);
                setAllButtonsDisabled(true);
                LOGGER.info("[handleOpponentTurn] Botones deshabilitados, turno del oponente.");
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
            String lastMove = (String) data.get("lastMove");
            Platform.runLater(() -> {
                Type boardType = new TypeToken<String[][]>(){}.getType();
                String[][] boardArray = gson.fromJson(boardJson, boardType);
                if (boardArray == null) {
                    LOGGER.severe("Error: Board array is null after parsing from JSON: " + boardJson);
                    return;
                }
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        buttons[i][j].setGraphic(null);
                    }
                }
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (boardArray[i][j] != null && !boardArray[i][j].isEmpty()) {
                            updateButtonGraphic(i, j, boardArray[i][j]);
                        }
                    }
                }
                if (lastMove != null) {
                    try {
                        String[] coords = lastMove.split(",");
                        if (coords.length == 2) {
                            int row = Integer.parseInt(coords[0]);
                            int col = Integer.parseInt(coords[1]);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Error parsing lastMove coordinates: " + lastMove + " - " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.severe("Error al procesar estado del juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void highlightButton(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            Button button = buttons[row][col];
            button.setStyle(button.getStyle() + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,255,0.8), 10, 0, 0, 0);");
            
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
                buttons[i][j].setGraphic(null);
            }
        }
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

    private void handleMoveError(Map<String, Object> data) {
        Platform.runLater(() -> {
            String message = data != null && data.containsKey("message") ? 
                String.valueOf(data.get("message")) : "Error al realizar el movimiento";
            statusLabel.setText("Error: " + message);
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
            
            setAllButtonsDisabled(true);
            
            clientConnection.sendRequest("move", moveData);
            
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
        Platform.runLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getGraphic() == null) {
                        buttons[i][j].setDisable(disabled);
                    }
                }
            }
        });
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

    public Scene getScene() {
        return scene;
    }
}

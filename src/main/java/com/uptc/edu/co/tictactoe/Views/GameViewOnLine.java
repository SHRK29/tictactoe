package com.uptc.edu.co.tictactoe.Views;

import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Network.ClientConnection;
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

public class GameViewOnLine {
    private String playerName;
    private String opponentName;
    private BorderPane rootLayout;
    private Scene scene;
    private GridPane gameGrid;
    private Button[][] gridButtons = new Button[3][3];
    private ClientConnection connection;
    private boolean isMyTurn;
    private char mySymbol; // 'X' o 'O'

    // Lógica del juego
    private char[][] boardState = new char[3][3];
    private boolean gameOver = false;

    // Para la línea ganadora
    private Pane linePane;
    private Line winningLine;
    private int[] winningCombination;

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

    public GameViewOnLine(String playerName, ClientConnection connection) {
        this.playerName = playerName;
        this.connection = connection;
        loadImages();
        rootLayout = createLayout();
        scene = new Scene(rootLayout, 1000, 700);
        initializeGame();
        scene.getStylesheets().add(getClass().getResource("/Styles/game.css").toExternalForm());
        startNetworkListener();
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

    private void initializeGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardState[i][j] = ' ';
                if (gridButtons[i][j] != null) {
                    gridButtons[i][j].setGraphic(null);
                    gridButtons[i][j].setText("");
                    gridButtons[i][j].setDisable(false);
                    gridButtons[i][j].getStyleClass().remove("cell-text-x");
                    gridButtons[i][j].getStyleClass().remove("cell-text-o");
                }
            }
        }
        gameOver = false;
        winningCombination = null;

        if (winningLine != null && linePane != null) {
            linePane.getChildren().remove(winningLine);
            winningLine = null;
        }
    }

    private BorderPane createLayout() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("game-background");
        layout.setPadding(new Insets(20));

        layout.setTop(createHeader());
        layout.setCenter(createGameCenter());
        layout.setBottom(createFooter());

        return layout;
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
        Label player2Label = new Label(opponentName != null ? opponentName.toUpperCase() : "ESPERANDO...");
        player2Label.getStyleClass().add("player-name");
        player2Label.setId("player2Name");
        ImageView player2Icon = createImageView(opponentIconImage, 240);
        player2Icon.getStyleClass().add("player-icon");
        player2Box.getChildren().addAll(player2Label, player2Icon);

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        headerBox.getChildren().addAll(leftSpacer, player1Box, vsLabel, player2Box, rightSpacer);
        return headerBox;
    }

    private HBox createGameCenter() {
        HBox centerContainer = new HBox(40);
        centerContainer.setAlignment(Pos.CENTER);

        ImageView oLogo = createImageView(imageO, 250);
        oLogo.setEffect(createNeonEffect(Color.rgb(255, 45, 241)));

        linePane = createLinePane();
        gameGrid = createGameGridPane();
        StackPane boardPane = new StackPane();
        boardPane.getStyleClass().add("board-container");
        boardPane.setAlignment(Pos.CENTER);
        boardPane.getChildren().addAll(linePane, gameGrid);

        ImageView xLogo = createImageView(imageX, 250);
        xLogo.setEffect(createNeonEffect(Color.rgb(0, 255, 238)));

        centerContainer.getChildren().addAll(oLogo, boardPane, xLogo);
        return centerContainer;
    }

    private HBox createFooter() {
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_LEFT);
        footerBox.setPadding(new Insets(20, 50, 20, 50));
        footerBox.setMaxWidth(Double.MAX_VALUE);

        Label titleLabel = new Label("TIC TAC TOE");
        titleLabel.getStyleClass().add("main-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button homeButton = new Button();
        ImageView homeIcon = createImageView(homeIconImage, ICON_SIZE_HOME);
        homeButton.setGraphic(homeIcon);
        homeButton.getStyleClass().add("home-button");
        homeButton.setOnAction(e -> {
            connection.sendMessage("QUIT");
            connection.closeConnection();
            App.mostrarLoginView();
        });

        footerBox.getChildren().addAll(titleLabel, spacer, homeButton);
        return footerBox;
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(30);
        glow.setSpread(0.5);
        glow.setInput(new Bloom(0.9));
        return glow;
    }

    private Pane createLinePane() {
        Pane pane = new Pane();
        pane.getStyleClass().add("line-pane");

        double totalWidth = 3 * CELL_SIZE + 2 * GRID_GAP;
        double totalHeight = 3 * CELL_SIZE + 2 * GRID_GAP;

        double verticalLineX1 = CELL_SIZE + GRID_GAP / 2.0;
        double verticalLineX2 = 2 * CELL_SIZE + 1.5 * GRID_GAP;

        Line verticalLine1 = createNeonLine(false, verticalLineX1, 0, verticalLineX1, totalHeight);
        Line verticalLine2 = createNeonLine(false, verticalLineX2, 0, verticalLineX2, totalHeight);

        double horizontalLineY1 = CELL_SIZE + GRID_GAP / 2.0;
        double horizontalLineY2 = 2 * CELL_SIZE + 1.5 * GRID_GAP;

        Line horizontalLine1 = createNeonLine(true, 0, horizontalLineY1, totalWidth, horizontalLineY1);
        Line horizontalLine2 = createNeonLine(true, 0, horizontalLineY2, totalWidth, horizontalLineY2);

        pane.getChildren().addAll(verticalLine1, verticalLine2, horizontalLine1, horizontalLine2);

        pane.setPrefSize(totalWidth, totalHeight);
        pane.setMaxSize(totalWidth, totalHeight);
        pane.setMinSize(totalWidth, totalHeight);

        return pane;
    }

    private Line createNeonLine(boolean horizontal, double startX, double startY, double endX, double endY) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(Color.rgb(246, 220, 67));
        line.setStrokeWidth(LINE_THICKNESS);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(246, 220, 67, 0.7));
        glow.setRadius(15);
        glow.setSpread(0.4);
        glow.setInput(new Bloom(0.8));

        line.setEffect(glow);
        line.setStrokeLineCap(StrokeLineCap.ROUND);

        return line;
    }

    private GridPane createGameGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GRID_GAP);
        grid.setVgap(GRID_GAP);
        grid.getStyleClass().add("game-grid");

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button cellButton = new Button();
                cellButton.setMinSize(CELL_SIZE, CELL_SIZE);
                cellButton.setPrefSize(CELL_SIZE, CELL_SIZE);
                cellButton.setMaxSize(CELL_SIZE, CELL_SIZE);
                cellButton.getStyleClass().add("grid-cell");

                final int r = row;
                final int c = col;
                cellButton.setOnAction(e -> handleCellClick(r, c));

                gridButtons[row][col] = cellButton;
                grid.add(cellButton, col, row);
            }
        }
        return grid;
    }

    private ImageView createImageView(Image image, double fitSize) {
        ImageView imageView = new ImageView();
        if (image != null && !image.isError()) {
            imageView.setImage(image);
            imageView.setFitWidth(fitSize);
            imageView.setFitHeight(fitSize);
            imageView.setPreserveRatio(true);
        }
        return imageView;
    }

    private void handleCellClick(int row, int col) {
        if (boardState[row][col] == ' ' && !gameOver && isMyTurn) {
            // Enviar movimiento al servidor
            connection.sendMessage("MOVE " + row + " " + col);
            // El servidor validará el movimiento y enviará la actualización a ambos jugadores
        }
    }

    private void updateBoard(int row, int col, char symbol) {
        Platform.runLater(() -> {
            boardState[row][col] = symbol;
            updateButtonGraphic(row, col, symbol);
            gridButtons[row][col].setDisable(true);
        });
    }

    private void updateButtonGraphic(int row, int col, char symbol) {
        ImageView imageView = null;
        double graphicSize = CELL_SIZE * 0.8;

        if (symbol == 'X' && imageX != null) {
            imageView = createImageView(imageX, graphicSize);
            imageView.setEffect(createNeonEffect(Color.rgb(0, 255, 238)));
        } else if (symbol == 'O' && imageO != null) {
            imageView = createImageView(imageO, graphicSize);
            imageView.setEffect(createNeonEffect(Color.rgb(255, 45, 241)));
        }

        if (imageView != null) {
            gridButtons[row][col].setGraphic(imageView);
            gridButtons[row][col].setText("");
            gridButtons[row][col].getStyleClass().remove("cell-text-x");
            gridButtons[row][col].getStyleClass().remove("cell-text-o");
        }
    }

    private void drawWinningLine() {
        if (winningCombination == null || linePane == null)
            return;

        double startX, startY, endX, endY;
        double cellCenterOffset = CELL_SIZE / 2.0;

        int startRow = winningCombination[0];
        int startCol = winningCombination[1];
        int endRow = winningCombination[2];
        int endCol = winningCombination[3];

        startX = startCol * (CELL_SIZE + GRID_GAP) + cellCenterOffset;
        startY = startRow * (CELL_SIZE + GRID_GAP) + cellCenterOffset;
        endX = endCol * (CELL_SIZE + GRID_GAP) + cellCenterOffset;
        endY = endRow * (CELL_SIZE + GRID_GAP) + cellCenterOffset;

        Platform.runLater(() -> {
            winningLine = new Line(startX, startY, endX, endY);
            winningLine.setStroke(Color.rgb(0, 255, 0));
            winningLine.setStrokeWidth(8.0);
            winningLine.setStrokeLineCap(StrokeLineCap.ROUND);

            DropShadow winningGlow = new DropShadow();
            winningGlow.setColor(Color.rgb(0, 255, 0, 0.8));
            winningGlow.setRadius(25);
            winningGlow.setSpread(0.6);
            winningGlow.setInput(new Bloom(1.0));

            winningLine.setEffect(winningGlow);
            winningLine.getStyleClass().add("winning-line");

            linePane.getChildren().add(winningLine);

            // Mostrar pantalla de victoria después de 0.5 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    Platform.runLater(() -> showWinScreen(boardState[startRow][startCol]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void showWinScreen(char winner) {
        String winnerName;
        Image winnerIcon;
        boolean isDraw = (winner == ' ');

        if (isDraw) {
            winnerName = "Empate";
            winnerIcon = imageO;
        } else {
            winnerName = (winner == mySymbol) ? playerName : opponentName;
            winnerIcon = (winner == mySymbol) ? imageX : imageO;
        }

        WinView winView = new WinView(winnerName, isDraw, winnerIcon);

        winView.getPlayAgainButton().setOnAction(e -> {
            connection.sendMessage("PLAY_AGAIN");
            initializeGame();
            App.cambiarEscena(this.scene, "Tic Tac Toe - Jugando Online");
        });

        winView.getMainMenuButton().setOnAction(e -> {
            connection.sendMessage("QUIT");
            connection.closeConnection();
            App.mostrarLoginView();
        });

        App.cambiarEscena(winView.getScene(), "Resultado - Tic Tac Toe");
    }

    private void startNetworkListener() {
        new Thread(() -> {
            while (!gameOver) {
                String message = connection.receiveMessage();
                if (message == null) continue;

                handleServerMessage(message);
            }
        }).start();
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split(" ");
        String command = parts[0];

        switch (command) {
            case "INIT":
                // INIT [symbol] [opponentName]
                Platform.runLater(() -> {
                    mySymbol = parts[1].charAt(0);
                    opponentName = parts[2];
                    isMyTurn = mySymbol == 'X';
                    rootLayout.setTop(createHeader()); // Actualizar header con nombre del oponente
                });
                break;

            case "MOVE":
                // MOVE [row] [col] [symbol]
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                char symbol = parts[3].charAt(0);
                updateBoard(row, col, symbol);
                isMyTurn = (symbol != mySymbol);
                break;

            case "WIN":
                // WIN [startRow] [startCol] [endRow] [endCol] [type] [index]
                gameOver = true;
                winningCombination = new int[6];
                for (int i = 0; i < 6; i++) {
                    winningCombination[i] = Integer.parseInt(parts[i + 1]);
                }
                drawWinningLine();
                break;

            case "DRAW":
                gameOver = true;
                showWinScreen(' ');
                break;

            case "OPPONENT_DISCONNECTED":
                Platform.runLater(() -> {
                    // Mostrar mensaje de desconexión y volver al menú principal
                    App.mostrarLoginView();
                });
                break;
        }
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        App.cambiarEscena(scene, "Tic Tac Toe - Jugando Online");
    }
}

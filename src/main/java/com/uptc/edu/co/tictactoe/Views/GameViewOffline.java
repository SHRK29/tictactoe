package com.uptc.edu.co.tictactoe.Views;

import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class GameViewOffline {

    private Stage primaryStage;
    private String playerName;
    private BorderPane rootLayout;
    private Scene scene;
    private GridPane gameGrid;
    private Button[][] gridButtons = new Button[3][3];
    private Label player1Label;
    private Label player2Label;
    private Label ticTacToeLabel;

    // Lógica del juego
    private char[][] boardState = new char[3][3];
    private boolean playerTurn = true;
    private boolean gameOver = false;
    private Random random = new Random();

    // Imágenes
    private Image playerIconImage;
    private Image pcIconImage;
    private Image homeIconImage;
    private Image imageX;
    private Image imageO;
    private Image vsImage;

    // Rutas a Imágenes
    private final String PATH_PLAYER_ICON = "/Icons/BluePlayer.png";
    private final String PATH_PC_ICON = "/Icons/RedPlayer.png";
    private final String PATH_HOME_ICON = "/Icons/Home.png";
    private final String PATH_X_IMAGE = "/Icons/X.png";
    private final String PATH_O_IMAGE = "/Icons/O.png";
    private final String PATH_VS_ICON = "/Icons/vs.png";

    // Constantes de Diseño
    private final double ICON_SIZE_PLAYER = 60.0;
    private final double ICON_SIZE_VS = 100.0;
    private final double ICON_SIZE_HOME = 120.0;
    private final double CELL_SIZE = 140.0;
    private final double GRID_GAP = 15.0;
    private final double LINE_THICKNESS = 4.0;

    public GameViewOffline(Stage primaryStage, String playerName) {
        this.primaryStage = primaryStage;
        this.playerName = playerName;
        loadImages();
        rootLayout = createLayout();
        scene = new Scene(rootLayout, 1000, 700);
        initializeGame();

        scene.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());
    }

    private void loadImages() {
        try {
            playerIconImage = cargarImagenSegura(PATH_PLAYER_ICON);
            pcIconImage = cargarImagenSegura(PATH_PC_ICON);
            homeIconImage = cargarImagenSegura(PATH_HOME_ICON);
            imageX = cargarImagenSegura(PATH_X_IMAGE);
            imageO = cargarImagenSegura(PATH_O_IMAGE);
            vsImage = cargarImagenSegura(PATH_VS_ICON);
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
        playerTurn = true;
        gameOver = false;
        // statusLabel.setText("Turno de: " + playerName.toUpperCase());
        // statusLabel.getStyleClass().remove("status-label-win");
        // statusLabel.getStyleClass().remove("status-label-lose");
        // statusLabel.getStyleClass().remove("status-label-draw");
        // statusLabel.getStyleClass().add("status-label");
    }

    private BorderPane createLayout() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("game-background");
        layout.setPadding(new Insets(20));

        // ----------------- HEADER -----------------
        HBox headerBox = new HBox();
        headerBox.getStyleClass().add("header-container");
        headerBox.setMaxWidth(Double.MAX_VALUE);

        // Jugador 1 (Imagen + Nombre)
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

        // Jugador 2 (Nombre + Imagen)
        HBox player2Box = new HBox(20);
        player2Box.getStyleClass().add("player-box");
        Label player2Label = new Label("PC");
        player2Label.getStyleClass().add("player-name");
        player2Label.setId("player2Name");
        ImageView player2Icon = createImageView(pcIconImage, 240);
        player2Icon.getStyleClass().add("player-icon");
        player2Box.getChildren().addAll(player2Label, player2Icon);

        // Espaciadores dinámicos
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

        layout.setTop(headerBox);

        // ----------------- SECCIÓN CENTRAL (TABLERO) -----------------
        HBox centerContainer = new HBox(40);
        centerContainer.setAlignment(Pos.CENTER);

        // Logo O
        ImageView oLogo = createImageView(imageO, 100);
        oLogo.setEffect(createNeonEffect(Color.rgb(255, 45, 241)));

        // Tablero
        Pane linePane = createLinePane();
        gameGrid = createGameGridPane();
        StackPane boardPane = new StackPane();
        boardPane.getStyleClass().add("board-container"); // Aplicar nueva clase
        boardPane.setAlignment(Pos.CENTER); // Centrar contenido
        boardPane.getChildren().addAll(linePane, gameGrid);

        // Logo X
        ImageView xLogo = createImageView(imageX, 100);
        xLogo.setEffect(createNeonEffect(Color.rgb(0, 255, 238)));

        centerContainer.getChildren().addAll(oLogo, boardPane, xLogo);
        layout.setCenter(centerContainer);

        // ----------------- SECCIÓN INFERIOR (FOOTER) -----------------
        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_LEFT); // Alinear contenido a los extremos
        footerBox.setPadding(new Insets(20, 50, 20, 50));
        footerBox.setMaxWidth(Double.MAX_VALUE); // Ocupar todo el ancho disponible

        // Título izquierda
        Label titleLabel = new Label("TIC TAC TOE");
        titleLabel.getStyleClass().add("main-title");

        // Espaciador para empujar el botón a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Botón Home derecha
        Button homeButton = new Button();
        ImageView homeIcon = createImageView(homeIconImage, ICON_SIZE_HOME);
        homeButton.setGraphic(homeIcon);
        homeButton.getStyleClass().add("home-button");
        homeButton.setOnAction(e -> navigateToLogin());

        footerBox.getChildren().addAll(titleLabel, spacer, homeButton); // Agregar espaciador en medio
        layout.setBottom(footerBox);

        return layout;
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(30);
        glow.setSpread(0.5);
        glow.setInput(new Bloom(0.9));
        return glow;
    }

    private void navigateToLogin() {
        Stage currentStage = (Stage) rootLayout.getScene().getWindow();
        LoginView loginView = new LoginView();
        Stage loginStage = new Stage();
        loginStage.setScene(loginView.getScene());
        WindowUtils.configurarVentanaPantallaCompleta(loginStage, false);
        loginStage.setTitle("Login");
        loginStage.show();
        currentStage.close();
    }

    private Pane createLinePane() {
        Pane pane = new Pane();
        pane.getStyleClass().add("line-pane");

        double totalWidth = 3 * CELL_SIZE + 2 * GRID_GAP;
        double totalHeight = 3 * CELL_SIZE + 2 * GRID_GAP;

        // Añadir ajuste vertical de 2px para compensar el margen del GridPane
        double verticalOffset = 2;

        // Líneas verticales
        double verticalLineX1 = CELL_SIZE + GRID_GAP / 2;
        double verticalLineX2 = 2 * CELL_SIZE + GRID_GAP * 1.5;
        Line verticalLine1 = createNeonLine(false, verticalLineX1, verticalOffset, verticalLineX1, totalHeight);
        Line verticalLine2 = createNeonLine(false, verticalLineX2, verticalOffset, verticalLineX2, totalHeight);

        // Líneas horizontales
        double horizontalLineY1 = CELL_SIZE + GRID_GAP / 2 + verticalOffset;
        double horizontalLineY2 = 2 * CELL_SIZE + GRID_GAP * 1.5 + verticalOffset;
        Line horizontalLine1 = createNeonLine(true, 0, horizontalLineY1, totalWidth, horizontalLineY1);
        Line horizontalLine2 = createNeonLine(true, 0, horizontalLineY2, totalWidth, horizontalLineY2);

        pane.getChildren().addAll(verticalLine1, verticalLine2, horizontalLine1, horizontalLine2);
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
                // Tamaño exacto y consistente
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
        } else {
            System.err.println("Advertencia: Imagen no cargada o inválida.");
            return new ImageView();
        }
        return imageView;
    }

    private void handleCellClick(int row, int col) {
        if (boardState[row][col] == ' ' && !gameOver && playerTurn) {
            boardState[row][col] = 'X';
            updateButtonGraphic(row, col, 'X');
            gridButtons[row][col].setDisable(true);

            if (checkWin('X')) {
                // statusLabel.setText(playerName.toUpperCase() + " GANA!");
                // statusLabel.getStyleClass().remove("status-label-lose");
                // statusLabel.getStyleClass().remove("status-label-draw");
                // statusLabel.getStyleClass().add("status-label-win");
                gameOver = true;
            } else if (checkDraw()) {
                // statusLabel.setText("EMPATE!");
                // statusLabel.getStyleClass().remove("status-label-lose");
                // statusLabel.getStyleClass().remove("status-label-win");
                // statusLabel.getStyleClass().add("status-label-draw");
                gameOver = true;
            } else {
                playerTurn = false;
                // statusLabel.setText("Turno de: PC");
                // statusLabel.getStyleClass().remove("status-label-win");
                // statusLabel.getStyleClass().remove("status-label-lose");
                // statusLabel.getStyleClass().remove("status-label-draw");
                // statusLabel.getStyleClass().add("status-label");

                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(this::pcMove);
                }).start();
            }
        }
    }

    private void pcMove() {
        if (gameOver || playerTurn)
            return;

        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (boardState[row][col] != ' ');

        boardState[row][col] = 'O';
        updateButtonGraphic(row, col, 'O');
        gridButtons[row][col].setDisable(true);

        if (checkWin('O')) {
            // statusLabel.setText("PC GANA!");
            // statusLabel.getStyleClass().remove("status-label-win");
            // statusLabel.getStyleClass().remove("status-label-draw");
            // statusLabel.getStyleClass().add("status-label-lose");
            gameOver = true;
        } else if (checkDraw()) {
            // statusLabel.setText("EMPATE!");
            // statusLabel.getStyleClass().remove("status-label-lose");
            // statusLabel.getStyleClass().remove("status-label-win");
            // statusLabel.getStyleClass().add("status-label-draw");
            gameOver = true;
        } else {
            playerTurn = true;
            // statusLabel.setText("Turno de: " + playerName.toUpperCase());
            // statusLabel.getStyleClass().remove("status-label-win");
            // statusLabel.getStyleClass().remove("status-label-lose");
            // statusLabel.getStyleClass().remove("status-label-draw");
            // statusLabel.getStyleClass().add("status-label");
        }
    }

    private void updateButtonGraphic(int row, int col, char player) {
        ImageView imageView = null;
        double graphicSize = CELL_SIZE * 0.8;

        if (player == 'X' && imageX != null) {
            imageView = createImageView(imageX, graphicSize);
            DropShadow glowX = new DropShadow();
            glowX.setColor(Color.rgb(0, 255, 238, 0.9));
            glowX.setRadius(30);
            glowX.setSpread(0.5);
            glowX.setInput(new Bloom(0.9));
            imageView.setEffect(glowX);

        } else if (player == 'O' && imageO != null) {
            imageView = createImageView(imageO, graphicSize);
            DropShadow glowO = new DropShadow();
            glowO.setColor(Color.rgb(255, 45, 241, 0.9));
            glowO.setRadius(30);
            glowO.setSpread(0.5);
            glowO.setInput(new Bloom(0.9));
            imageView.setEffect(glowO);
        }

        if (imageView != null) {
            gridButtons[row][col].setGraphic(imageView);
            gridButtons[row][col].setText("");
            gridButtons[row][col].getStyleClass().remove("cell-text-x");
            gridButtons[row][col].getStyleClass().remove("cell-text-o");
        } else {
            gridButtons[row][col].setText(String.valueOf(player));
            gridButtons[row][col].getStyleClass().add(player == 'X' ? "cell-text-x" : "cell-text-o");
            gridButtons[row][col].setGraphic(null);
        }
    }

    private boolean checkWin(char player) {
        // Comprobar filas
        for (int i = 0; i < 3; i++) {
            if (boardState[i][0] == player && boardState[i][1] == player && boardState[i][2] == player) {
                return true;
            }
        }
        // Comprobar columnas
        for (int j = 0; j < 3; j++) {
            if (boardState[0][j] == player && boardState[1][j] == player && boardState[2][j] == player) {
                return true;
            }
        }
        // Comprobar diagonales
        if (boardState[0][0] == player && boardState[1][1] == player && boardState[2][2] == player) {
            return true;
        }
        if (boardState[0][2] == player && boardState[1][1] == player && boardState[2][0] == player) {
            return true;
        }
        return false;
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] == ' ') {
                    return false;
                }
            }
        }
        return !checkWin('X') && !checkWin('O');
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Tic Tac Toe - Jugando contra PC");
        stage.setScene(scene);
        WindowUtils.configurarVentanaPantallaCompleta(stage, true);

        stage.setOnCloseRequest(e -> {
            if (primaryStage != null) {
                primaryStage.show();
            }
        });

        if (primaryStage != null) {
            primaryStage.hide();
        }

        stage.show();
    }
}
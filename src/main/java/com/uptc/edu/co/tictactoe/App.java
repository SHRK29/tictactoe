package com.uptc.edu.co.tictactoe;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

import com.uptc.edu.co.tictactoe.Network.ClientConnection;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;
import com.uptc.edu.co.tictactoe.Views.GameViewOffline;
import com.uptc.edu.co.tictactoe.Views.GameViewOnLine;
import com.uptc.edu.co.tictactoe.Views.LoginView;
import com.uptc.edu.co.tictactoe.Views.ConnectionErrorView;
import com.uptc.edu.co.tictactoe.Views.LoadingView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static Stage primaryStage;
    private static ClientConnection clientConnection;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    static {
        // Configurar el logger raíz para un nivel por defecto (INFO o WARNING sería bueno)
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO); // Cambiado de ALL a INFO

        // Configurar niveles específicos para loggers de JavaFX para reducir el ruido
        Logger.getLogger("javafx.scene").setLevel(Level.WARNING);
        Logger.getLogger("javafx.fxml").setLevel(Level.WARNING);
        Logger.getLogger("javafx.css").setLevel(Level.WARNING);
        Logger.getLogger("javafx.scene.layout").setLevel(Level.WARNING);
        Logger.getLogger("javafx.scene.control").setLevel(Level.WARNING);
        Logger.getLogger("javafx.scene.Node").setLevel(Level.WARNING); // Específicamente para los mensajes de Node

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL); // El handler puede manejar todos los niveles, pero el logger decidirá qué pasar.
        handler.setFormatter(new SimpleFormatter());

        // Limpiar handlers existentes para evitar duplicados si este bloque se ejecuta múltiples veces (poco probable para static)
        if (rootLogger.getHandlers().length > 0) {
            for (java.util.logging.Handler h : rootLogger.getHandlers()) {
                rootLogger.removeHandler(h);
            }
        }
        rootLogger.addHandler(handler);
    }

    @Override
    public void start(Stage stage) {
        LOGGER.info("Iniciando aplicación TicTacToe");
        primaryStage = stage;
        primaryStage.setTitle("TicTacToe");
        mostrarLoginView();
        primaryStage.show();
    }

    public static void mostrarLoginView() {
        LOGGER.info("Mostrando pantalla de login");
        LoginView loginView = new LoginView();
        Scene scene = loginView.getScene();
        cambiarEscena(scene, "TicTacToe - Login");
    }

    public static void iniciarPartidaOffline(String playerName) {
        LOGGER.info("Iniciando partida offline para jugador: " + playerName);
        GameViewOffline gameView = new GameViewOffline(playerName);
        Scene scene = gameView.getScene();
        cambiarEscena(scene, "TicTacToe - Modo Offline");
    }

    public static void iniciarPartidaOnline(String playerName) {
        LOGGER.info("Intentando iniciar partida online para jugador: " + playerName);
        
        // Mostrar pantalla de carga
        Platform.runLater(() -> {
            LoadingView loadingView = new LoadingView();
            loadingView.show();
        });

        // Iniciar conexión en un hilo separado
        new Thread(() -> {
            try {
                LOGGER.info("Conectando al servidor " + SERVER_HOST + ":" + SERVER_PORT);
                
                // Conectar al servidor
                LOGGER.fine("Creando socket de conexión...");
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                LOGGER.info("Socket creado exitosamente");

                LOGGER.fine("Inicializando conexión del cliente...");
                clientConnection = new ClientConnection(socket);
                LOGGER.info("Conexión del cliente inicializada");

                // Enviar solicitud de login
                LOGGER.fine("Enviando solicitud de login para " + playerName);
                Map<String, Object> loginData = new HashMap<>();
                loginData.put("playerName", playerName);
                clientConnection.sendRequest("login", loginData);
                LOGGER.info("Solicitud de login enviada exitosamente");

                // Crear vista del juego
                Platform.runLater(() -> {
                    LOGGER.fine("Creando vista del juego online...");
                    GameViewOnLine gameView = new GameViewOnLine(playerName, clientConnection);
                    Scene scene = gameView.getScene();
                    cambiarEscena(scene, "TicTacToe - Modo Online");
                    LOGGER.info("Vista del juego online creada y mostrada");
                    
                    // Cerrar la pantalla de carga
                    LoadingView.close();
                });
                
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error de conexión: {0}", e.getMessage());
                LOGGER.log(Level.FINE, "Detalles del error:", e);
                
                Platform.runLater(() -> {
                    // Cerrar la pantalla de carga
                    LoadingView.close();
                    
                    LOGGER.info("Mostrando ventana de error de conexión");
                    ConnectionErrorView errorView = new ConnectionErrorView();
                    errorView.show();
                });
            }
        }).start();
    }

    public static void cambiarEscena(Scene scene, String title) {
        LOGGER.fine("Cambiando escena a: " + title);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage);
        LOGGER.fine("Escena cambiada exitosamente");
    }

    @Override
    public void stop() {
        LOGGER.info("Cerrando aplicación");
        if (clientConnection != null) {
            LOGGER.fine("Cerrando conexión con el servidor");
            clientConnection.close();
        }
        LOGGER.info("Aplicación cerrada exitosamente");
    }

    public static void main(String[] args) {
        LOGGER.info("Iniciando aplicación TicTacToe");
        launch();
    }
}
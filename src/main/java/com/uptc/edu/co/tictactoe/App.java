package com.uptc.edu.co.tictactoe;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.uptc.edu.co.tictactoe.Network.ClientConnection;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;
import com.uptc.edu.co.tictactoe.Views.GameViewOffline;
import com.uptc.edu.co.tictactoe.Views.GameViewOnLine;
import com.uptc.edu.co.tictactoe.Views.LoginView;
import com.uptc.edu.co.tictactoe.Views.ConnectionErrorView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static ClientConnection clientConnection;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("TicTacToe");
        mostrarLoginView();
        primaryStage.show();

    }

    public static void mostrarLoginView() {
        LoginView loginView = new LoginView();
        Scene scene = loginView.getScene();
        cambiarEscena(scene, "TicTacToe - Login");
    }

    public static void iniciarPartidaOffline(String playerName) {
        GameViewOffline gameView = new GameViewOffline(playerName);
        Scene scene = gameView.getScene();
        cambiarEscena(scene, "TicTacToe - Modo Offline");
    }

    public static void iniciarPartidaOnline(String playerName) {
        try {
            // Conectar al servidor
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            clientConnection = new ClientConnection(socket);

            // Enviar solicitud de login
            Map<String, Object> loginData = new HashMap<>();
            loginData.put("playerName", playerName);
            clientConnection.sendRequest("login", loginData);

            // Crear vista del juego
            GameViewOnLine gameView = new GameViewOnLine(playerName, clientConnection);
            Scene scene = gameView.getScene();
            cambiarEscena(scene, "TicTacToe - Modo Online");
        } catch (IOException e) {
            System.err.println("Error conectando al servidor: " + e.getMessage());
            Platform.runLater(() -> {
                ConnectionErrorView errorView = new ConnectionErrorView();
                errorView.show();
            });
        }
    }

    public static void cambiarEscena(Scene scene, String title) {
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage);
    }

    @Override
    public void stop() {
        if (clientConnection != null) {
            clientConnection.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
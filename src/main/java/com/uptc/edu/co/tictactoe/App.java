package com.uptc.edu.co.tictactoe;

import com.uptc.edu.co.tictactoe.Network.ClientConnection;
import com.uptc.edu.co.tictactoe.Views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static ClientConnection clientConnection;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        initializeNetwork();
        mostrarLoginView();
    }

    private void initializeNetwork() {
        clientConnection = new ClientConnection();
        clientConnection.startConnection("localhost", 5555);
    }

    public static void mostrarLoginView() {
        LoginView vistaLogin = new LoginView();
        cambiarEscena(vistaLogin.getScene(), "Tic Tac Toe - Login");
    }

    public static void cambiarEscena(Scene nuevaEscena, String titulo) {
        primaryStage.setScene(nuevaEscena);
        primaryStage.setTitle(titulo);
        primaryStage.show();
    }

    public static ClientConnection getClientConnection() {
        return clientConnection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
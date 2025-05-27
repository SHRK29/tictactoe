package com.uptc.edu.co.tictactoe;

import com.uptc.edu.co.tictactoe.Views.LoginView;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        FontUtils.cargarFuenteBaloo(1);
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage);
        primaryStage.setTitle("Tic Tac Toe - UPTC");
        showLoginView();
        primaryStage.show();
    }

    public static void showLoginView() {
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
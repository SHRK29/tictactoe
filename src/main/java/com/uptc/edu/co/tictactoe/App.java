package com.uptc.edu.co.tictactoe;

import com.uptc.edu.co.tictactoe.Views.ConnectionErrorView;
import com.uptc.edu.co.tictactoe.Views.DisconnectedView;
import com.uptc.edu.co.tictactoe.Views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font; // <-- Importación faltante
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Precargar la fuente con verificación
        try {
            Font font = Font.loadFont(getClass().getResourceAsStream("/Fonts/Baloo2-ExtraBold.ttf"), 12);
            if (font == null) {
                System.err.println("¡Error: La fuente no se cargó correctamente!");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente: " + e.getMessage());
        }

        LoginView loginView = new LoginView();
        primaryStage.setScene(loginView.getScene());
        primaryStage.show();
    }
}
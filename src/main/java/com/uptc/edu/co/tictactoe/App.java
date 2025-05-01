package com.uptc.edu.co.tictactoe;

import com.uptc.edu.co.tictactoe.Views.LoginView;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Precargar la fuente al inicio
        FontUtils.cargarFuenteBaloo(1);
        
        // Configurar y mostrar la ventana principal
        LoginView loginView = new LoginView();
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage, false); // false porque es Stage existente
        
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("Tic Tac Toe - UPTC");
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Precarga temprana opcional
        FontUtils.cargarFuenteBaloo(1);
        launch(args);
    }
}
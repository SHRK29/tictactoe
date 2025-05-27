package com.uptc.edu.co.tictactoe.Utils;

import javafx.stage.Stage;

public class WindowUtils {

    /**
     * Configura una ventana como pantalla completa sin bordes
     * 
     * @param stage La ventana a configurar
     */
    public static void configurarVentanaPantallaCompleta(Stage stage) {
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.setMaximized(true);
    }
}
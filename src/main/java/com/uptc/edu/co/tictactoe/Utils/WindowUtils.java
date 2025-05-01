package com.uptc.edu.co.tictactoe.Utils;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowUtils {

    /**
     * Configura una ventana como pantalla completa sin bordes
     * @param stage La ventana a configurar
     * @param isNewStage Indica si es un Stage nuevo (true) o existente (false)
     */
    public static void configurarVentanaPantallaCompleta(Stage stage, boolean isNewStage) {
        if (isNewStage) {
            stage.initStyle(StageStyle.UNDECORATED); // Solo para Stages nuevos
        }
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }
}
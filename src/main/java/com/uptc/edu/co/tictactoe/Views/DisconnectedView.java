package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;

public class DisconnectedView {

    public void show() {
        // Configuración del layout principal
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("disconnected-root");

        // Mensaje principal con fuente cargada mediante FontUtils
        Label message = new Label("DESCONEXIÓN DEL SERVIDOR");
        message.setFont(FontUtils.cargarFuenteBaloo(36));
        message.getStyleClass().add("disconnected-message");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);

        // Icono de advertencia
        ImageView warningIcon = new ImageView(new Image(getClass().getResource("/icons/Warning.png").toExternalForm()));
        warningIcon.setPreserveRatio(true);
        warningIcon.getStyleClass().add("warning-icon");
        warningIcon.setFitWidth(200);

        root.getChildren().addAll(message, warningIcon);

        // Configuración de la escena
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/disconnected.css").toExternalForm());

        Stage stage = new Stage();
        WindowUtils.configurarVentanaPantallaCompleta(stage, true);
        stage.setTitle("Error de conexión");
        stage.setScene(scene);
        stage.show();

        // Ajustes responsive
        warningIcon.fitWidthProperty().bind(scene.widthProperty().divide(4));
    }
}
package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets; 
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import com.uptc.edu.co.tictactoe.Utils.WindowUtils;

public class ConnectionErrorView {

    public void show() {
        // Cargar fuentes con tamaños específicos
        Font titleFont = FontUtils.cargarFuenteBaloo(42);  // Tamaño para título
        Font subtitleFont = FontUtils.cargarFuenteBaloo(24); // Tamaño para subtítulo
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);  // Tamaño para botón

        // Título
        Label title = new Label("ERROR DE CONEXIÓN");
        title.setFont(titleFont);
        title.setTextFill(Color.web("#FFB3F9"));
        title.setEffect(new DropShadow(10, Color.web("#FF2DF1")));

        // Subtítulo
        Label subtitle = new Label("VERIFICA TU CONEXIÓN A INTERNET");
        subtitle.setFont(subtitleFont);
        subtitle.setTextFill(Color.web("#FFB3F9"));
        subtitle.setEffect(new DropShadow(8, Color.web("#FF2DF1")));

        // Imagen central
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/Icons/ErrorClose.png")));
        icon.setFitWidth(300);
        icon.setFitHeight(300);
        icon.setPreserveRatio(true);

        // Botón de reintento (estilo idéntico a LoginView)
        Button retryButton = new Button("REINTENTAR");
        retryButton.setFont(buttonFont);
        retryButton.getStyleClass().add("neon-button");
        retryButton.setOnAction(e -> {
            ((Stage) retryButton.getScene().getWindow()).close();
            // Aquí podrías agregar lógica de reconexión
        });

        // Layout principal
        VBox root = new VBox(25, title, subtitle, icon, retryButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("error-root");

        // Configuración de escena
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/error.css").toExternalForm());

        Stage stage = new Stage();
        WindowUtils.configurarVentanaPantallaCompleta(stage, true);
        stage.setTitle("Error de Conexión");
        stage.setScene(scene);
        stage.show();
    }
}
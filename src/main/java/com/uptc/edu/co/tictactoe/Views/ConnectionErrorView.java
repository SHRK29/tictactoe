package com.uptc.edu.co.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import java.io.InputStream;

public class ConnectionErrorView {
    private Scene scene;
    private final double ICON_SIZE = 200.0;

    public ConnectionErrorView() {
        createView();
    }

    private void createView() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("background");

        // Icono de error
        ImageView errorIcon = createImageView("/Icons/ErrorClose.png", ICON_SIZE);
        if (errorIcon != null) {
            errorIcon.setEffect(createNeonEffect(Color.rgb(255, 45, 45)));
            root.getChildren().add(errorIcon);
        }

        // Mensaje de error
        Text errorText = new Text("ERROR DE CONEXIÓN");
        errorText.setFont(FontUtils.cargarFuenteBaloo(42));
        errorText.setFill(Color.web("#FFB3F9"));
        errorText.setEffect(createNeonEffect(Color.web("#FF2DF1")));

        Text subtitleText = new Text("NO SE PUDO CONECTAR AL SERVIDOR");
        subtitleText.setFont(FontUtils.cargarFuenteBaloo(24));
        subtitleText.setFill(Color.web("#FFB3F9"));
        subtitleText.setEffect(createNeonEffect(Color.web("#FF2DF1")));

        // Botón de reintentar
        Button retryButton = new Button("REINTENTAR");
        retryButton.getStyleClass().add("neon-button");
        retryButton.setFont(FontUtils.cargarFuenteBaloo(20));
        retryButton.setOnAction(e -> App.mostrarLoginView());

        root.getChildren().addAll(errorText, subtitleText, retryButton);
        
        scene = new Scene(root, 800, 600);
        String cssPath = "/Styles/login.css";
        if (getClass().getResource(cssPath) != null) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }

    private ImageView createImageView(String resourcePath, double size) {
        try {
            InputStream is = getClass().getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("No se pudo cargar el recurso: " + resourcePath);
                return null;
            }
            Image image = new Image(is);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen " + resourcePath + ": " + e.getMessage());
            return null;
        }
    }

    private DropShadow createNeonEffect(Color color) {
        DropShadow neonEffect = new DropShadow();
        neonEffect.setColor(color);
        neonEffect.setSpread(0.5);
        neonEffect.setRadius(20);
        return neonEffect;
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        App.cambiarEscena(scene, "Error de Conexión");
    }
}
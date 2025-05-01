package com.uptc.edu.co.tictactoe.Views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import com.uptc.edu.co.tictactoe.Utils.WindowUtils;

public class LoadingView {

    private static final int TOTAL_FRAMES = 10;
    private static final int FRAME_RATE = 150; // ms por frame

    public void show() {
        List<Image> frames = new ArrayList<>();

        // Cargar todos los frames
        for (int i = 1; i <= TOTAL_FRAMES; i++) {
            String imagePath = "/Icons/Loading/Frame" + i + ".png";
            try {
                Image img = new Image(getClass().getResourceAsStream(imagePath));
                if (img.isError()) {
                    System.err.println("Error cargando imagen: " + imagePath);
                } else {
                    frames.add(img);
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar: " + imagePath);
            }
        }

        if (frames.isEmpty()) {
            System.err.println("No se cargaron imágenes para la animación.");
            return;
        }

        ImageView imageView = new ImageView(frames.get(0));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(frames.get(0).getWidth());
        imageView.setFitHeight(frames.get(0).getHeight());

        Timeline animation = new Timeline();
        for (int i = 0; i < frames.size(); i++) {
            final int index = i;
            animation.getKeyFrames().add(
                new KeyFrame(Duration.millis(FRAME_RATE * i), e -> {
                    imageView.setImage(frames.get(index));
                })
            );
        }
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        StackPane root = new StackPane(imageView);
        root.setStyle("-fx-background-color: #1A0029;");

        Scene scene = new Scene(root, imageView.getFitWidth(), imageView.getFitHeight());
        Stage stage = new Stage();
        WindowUtils.configurarVentanaPantallaCompleta(stage, true);

        stage.setTitle("Cargando...");
        stage.setScene(scene);
        stage.show();
    }
}

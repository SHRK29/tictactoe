package com.uptc.edu.co.tictactoe.Views;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.uptc.edu.co.tictactoe.App;

import java.util.ArrayList;
import java.util.List;

public class LoadingView {

    private static final int TOTAL_FRAMES = 10;
    private static final int FRAME_RATE = 85; // ms por frame
    private Scene scene;
    private Timeline animation;

    public LoadingView() {
        List<Image> frames = loadAnimationFrames();

        if (frames.isEmpty()) {
            System.err.println("No se cargaron imágenes para la animación.");
            return;
        }

        ImageView imageView = createImageView(frames.get(0));
        this.animation = createAnimation(imageView, frames);

        StackPane root = new StackPane(imageView);
        root.setStyle("-fx-background-color: #410445;");

        this.scene = new Scene(root, imageView.getFitWidth(), imageView.getFitHeight());
    }

    private List<Image> loadAnimationFrames() {
        List<Image> frames = new ArrayList<>();
        for (int i = 1; i <= TOTAL_FRAMES; i++) {
            String imagePath = "/Icons/Loading/Frame" + i + ".png";
            try {
                Image img = new Image(getClass().getResourceAsStream(imagePath));
                if (!img.isError()) {
                    frames.add(img);
                } else {
                    System.err.println("Error cargando imagen: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar: " + imagePath);
            }
        }
        return frames;
    }

    private ImageView createImageView(Image initialFrame) {
        ImageView imageView = new ImageView(initialFrame);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(initialFrame.getWidth());
        imageView.setFitHeight(initialFrame.getHeight());
        return imageView;
    }

    private Timeline createAnimation(ImageView imageView, List<Image> frames) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < frames.size(); i++) {
            final int index = i;
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(FRAME_RATE * i), e -> {
                        imageView.setImage(frames.get(index));
                    }));
        }
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }

    public void show() {
        if (scene != null) {
            App.cambiarEscena(scene, "Cargando...");
            if (animation != null) {
                animation.play();
            }
        }
    }

    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
        }
    }

    public Scene getScene() {
        return scene;
    }
}
package com.uptc.edu.co.tictactoe.Views;

import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HowToPlayView {
    private Scene scene;

    public HowToPlayView() {
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);

        // Cargar imágenes del slider
        List<Image> slides = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Image img = new Image(getClass().getResourceAsStream("/Images/" + i + ".png"));
            slides.add(img);
        }

        // Imagen principal
        ImageView imageView = new ImageView(slides.get(0));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setEffect(new DropShadow(20, Color.web("#FF2DF1")));
        imageView.setFitWidth(700); // Valor inicial
        imageView.setFitHeight(400);

        // Flecha izquierda
        ImageView leftArrow = new ImageView(new Image(getClass().getResourceAsStream("/Icons/izqArrow.png")));
        leftArrow.setFitHeight(150);
        leftArrow.setPreserveRatio(true);
        leftArrow.setPickOnBounds(true);
        leftArrow.setStyle("-fx-cursor: hand;");
        leftArrow.setEffect(new DropShadow(12, Color.web("#F6DC43")));
        leftArrow.setOnMouseClicked((MouseEvent e) -> {
            int current = slides.indexOf(imageView.getImage());
            int prev = (current - 1 + slides.size()) % slides.size();
            imageView.setImage(slides.get(prev));
        });

        // Flecha derecha
        ImageView rightArrow = new ImageView(new Image(getClass().getResourceAsStream("/Icons/derArrow.png")));
        rightArrow.setFitHeight(150);
        rightArrow.setPreserveRatio(true);
        rightArrow.setPickOnBounds(true);
        rightArrow.setStyle("-fx-cursor: hand;");
        rightArrow.setEffect(new DropShadow(12, Color.web("#F6DC43")));
        rightArrow.setOnMouseClicked((MouseEvent e) -> {
            int current = slides.indexOf(imageView.getImage());
            int next = (current + 1) % slides.size();
            imageView.setImage(slides.get(next));
        });

        // Layout con imagen y flechas
        HBox imageSlider = new HBox(20, leftArrow, imageView, rightArrow);
        imageSlider.setAlignment(Pos.CENTER);
        HBox.setMargin(leftArrow, new Insets(0, 20, 0, 20));
        HBox.setMargin(rightArrow, new Insets(0, 20, 0, 20));

        // Slider automático
        Timeline slider = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            int nextIndex = (slides.indexOf(imageView.getImage()) + 1) % slides.size();
            imageView.setImage(slides.get(nextIndex));
        }));
        slider.setCycleCount(Timeline.INDEFINITE);
        slider.play();

        // Botón VOLVER
        Button backButton = crearBotonNeon("VOLVER", buttonFont);
        backButton.setOnAction(e -> {
            slider.stop();
            App.mostrarLoginView();
        });

        // Layout principal
        VBox layout = new VBox(30, imageSlider, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("how-root");

        // Crear escena
        this.scene = new Scene(layout, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/howtoplay.css").toExternalForm());

        // Redimensión automática de la imagen
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue();
            imageView.setFitWidth(width * 0.7);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            double height = newVal.doubleValue();
            imageView.setFitHeight(height * 0.5);
        });
    }

    private Button crearBotonNeon(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.getStyleClass().add("neon-button");
        button.setPrefSize(280, 60);

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #410445, #2D0230); " +
                    "-fx-text-fill: #FF2DF1;");
            button.setEffect(new DropShadow(15, Color.web("#F6DC43")));
            button.setTranslateY(-2);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("");
            button.setEffect(new DropShadow(10, Color.web("#F6DC43")));
            button.setTranslateY(0);
        });

        return button;
    }

    public Scene getScene() {
        return scene;
    }
}

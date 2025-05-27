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

import com.uptc.edu.co.tictactoe.App;
import com.uptc.edu.co.tictactoe.Utils.FontUtils;

public class ConnectionErrorView {
    private Stage primaryStage;

    public ConnectionErrorView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        Font titleFont = FontUtils.cargarFuenteBaloo(42);
        Font subtitleFont = FontUtils.cargarFuenteBaloo(24);
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);

        Label title = new Label("ERROR DE CONEXIÓN");
        title.setFont(titleFont);
        title.setTextFill(Color.web("#FFB3F9"));
        title.setEffect(new DropShadow(10, Color.web("#FF2DF1")));

        Label subtitle = new Label("VERIFICA TU CONEXIÓN A INTERNET");
        subtitle.setFont(subtitleFont);
        subtitle.setTextFill(Color.web("#FFB3F9"));
        subtitle.setEffect(new DropShadow(8, Color.web("#FF2DF1")));

        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/Icons/ErrorClose.png")));
        icon.setFitWidth(300);
        icon.setFitHeight(300);

        Button retryButton = new Button("REINTENTAR");
        retryButton.setFont(buttonFont);
        retryButton.getStyleClass().add("neon-button");
        retryButton.setOnAction(e -> {
            LoginView loginView = new LoginView(this.primaryStage);
            App.getPrimaryStage().setScene(loginView.getScene());
        });

        VBox root = new VBox(25, title, subtitle, icon, retryButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("error-root");

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/error.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Error de Conexión");
    }
}
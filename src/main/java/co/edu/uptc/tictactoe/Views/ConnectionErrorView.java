package co.edu.uptc.tictactoe.Views;

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

import co.edu.uptc.tictactoe.App;
import co.edu.uptc.tictactoe.Utils.FontUtils;

public class ConnectionErrorView {
    private Scene scene;

    public ConnectionErrorView() {
        // Configurar la escena
        configurarEscena();
    }

    private void configurarEscena() {
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
        icon.setPreserveRatio(true);

        Button retryButton = crearBotonReintentar(buttonFont);

        VBox root = new VBox(25, title, subtitle, icon, retryButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("error-root");

        this.scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/error.css").toExternalForm());
    }

    private Button crearBotonReintentar(Font font) {
        Button button = new Button("REINTENTAR");
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
        
        button.setOnAction(e -> {
            App.mostrarLoginView();
        });
        
        return button;
    }

    public void show() {
        App.cambiarEscena(scene, "Error de Conexión");
    }

    public Scene getScene() {
        return scene;
    }
}
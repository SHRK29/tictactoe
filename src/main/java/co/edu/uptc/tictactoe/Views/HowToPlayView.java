package co.edu.uptc.tictactoe.Views;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import co.edu.uptc.tictactoe.App;
import co.edu.uptc.tictactoe.Utils.FontUtils;

public class HowToPlayView {
    private Scene scene;

    public HowToPlayView() {
        // Cargar fuentes
        Font titleFont = FontUtils.cargarFuenteBaloo(42);
        Font textFont = FontUtils.cargarFuenteBaloo(20);
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);

        // Título
        Label title = new Label("¿CÓMO JUGAR?");
        title.setFont(titleFont);
        title.getStyleClass().add("how-title");

        // Instrucciones
        Label instructions = new Label(
                "• El juego es para 2 jugadores o contra la PC\n" +
                "• Gana quien alinee 3 símbolos (X u O)\n" +
                "• Puedes jugar en línea o en local\n" +
                "• Usa el mouse para hacer tu jugada");
        instructions.setFont(textFont);
        instructions.getStyleClass().add("how-text");
        instructions.setMaxWidth(500);
        instructions.setWrapText(true);

        // Botón VOLVER
        Button backButton = crearBotonNeon("VOLVER", buttonFont);
        backButton.setOnAction(e -> {
            App.mostrarLoginView();
        });

        // Layout
        VBox root = new VBox(30, title, instructions, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("how-root");

        this.scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/howtoplay.css").toExternalForm());
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
package co.edu.uptc.tictactoe.Views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import co.edu.uptc.tictactoe.Utils.FontUtils;

public class WinView {
    private Scene scene;
    private Button playAgainButton;
    private Button mainMenuButton;
    
    public WinView(String winnerName, boolean isDraw, Image winnerIcon) {
        // Cargar fuentes
        Font balooFontLarge = FontUtils.cargarFuenteBaloo(60);
        Font balooFontMedium = FontUtils.cargarFuenteBaloo(28);
        
        // Layout principal
        VBox mainLayout = new VBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getStyleClass().add("win-background");
        mainLayout.setPadding(new Insets(40));
        
        // Mensaje de resultado
        Text resultText;
        if (isDraw) {
            resultText = new Text("¡EMPATE!");
        } else {
            resultText = new Text("¡" + winnerName.toUpperCase() + " GANA!");
        }
        resultText.setFont(balooFontLarge);
        resultText.setFill(Color.web("#F6DC43"));
        resultText.setEffect(new DropShadow(15, Color.web("#FF2DF1")));
        
        // Icono del ganador
        ImageView winnerImageView = new ImageView(winnerIcon);
        winnerImageView.setFitHeight(200);
        winnerImageView.setFitWidth(200);
        winnerImageView.setPreserveRatio(true);
        
        // Contenedor de botones
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Botón Jugar de nuevo
        playAgainButton = new Button("JUGAR DE NUEVO");
        styleButton(playAgainButton, balooFontMedium);
        
        // Botón Menú principal
        mainMenuButton = new Button("MENÚ PRINCIPAL");
        styleButton(mainMenuButton, balooFontMedium);
        
        buttonBox.getChildren().addAll(playAgainButton, mainMenuButton);
        
        // Añadir componentes al layout
        mainLayout.getChildren().addAll(resultText, winnerImageView, buttonBox);
        
        scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/Styles/win.css").toExternalForm());
    }
    
    private void styleButton(Button button, Font font) {
        button.setFont(font);
        button.getStyleClass().add("win-button");
        button.setPrefSize(280, 60);
        
        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(15, Color.web("#F6DC43")));
            button.setTranslateY(-2);
        });
        
        button.setOnMouseExited(e -> {
            button.setEffect(new DropShadow(10, Color.web("#F6DC43")));
            button.setTranslateY(0);
        });
        
        button.setOnMousePressed(e -> {
            button.setEffect(new DropShadow(5, Color.web("#F6DC43")));
            button.setTranslateY(1);
        });
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public Button getPlayAgainButton() {
        return playAgainButton;
    }
    
    public Button getMainMenuButton() {
        return mainMenuButton;
    }
}
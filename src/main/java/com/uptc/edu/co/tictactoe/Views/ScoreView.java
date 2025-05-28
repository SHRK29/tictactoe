package com.uptc.edu.co.tictactoe.Views;

import com.uptc.edu.co.tictactoe.Utils.FontUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScoreView {
    private Scene scene;
    private Button backButton;

    public ScoreView() {
        // Fuente Baloo 2 ExtraBold
        Font buttonFont = FontUtils.cargarFuenteBaloo(18);

        // Título
        Label title = new Label("HISTORIAL DE PARTIDAS");
        title.setFont(FontUtils.cargarFuenteBaloo(42));
        title.setTextFill(Color.web("#FF2DF1"));
        title.setEffect(new DropShadow(15, Color.web("#FF2DF1")));

        // Tabla
        TableView<PlayerScore> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("table-view");

        TableColumn<PlayerScore, String> nameColumn = new TableColumn<>("NOMBRE");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PlayerScore, Integer> scoreColumn = new TableColumn<>("PARTIDAS GANADAS");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        table.getColumns().addAll(nameColumn, scoreColumn);

        ObservableList<PlayerScore> data = FXCollections.observableArrayList(
                new PlayerScore("Jugador1", 5),
                new PlayerScore("Jugador2", 3),
                new PlayerScore("Jugador3", 7));
        table.setItems(data);

        // Botón volver con estilo
        backButton = new Button("VOLVER");
        backButton.setFont(buttonFont);
        backButton.getStyleClass().add("neon-button");

        // Layout
        VBox layout = new VBox(20, title, table, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("background");

        scene = new Scene(layout, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/Styles/score.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/Styles/login.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    public Button getBackButton() {
        return backButton;
    }

    public static class PlayerScore {
        private final String name;
        private final int score;

        public PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}

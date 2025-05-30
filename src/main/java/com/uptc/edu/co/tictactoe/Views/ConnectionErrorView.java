package com.uptc.edu.co.tictactoe.Views;

import com.uptc.edu.co.tictactoe.App;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.logging.Logger;

public class ConnectionErrorView {
    private static final Logger LOGGER = Logger.getLogger(ConnectionErrorView.class.getName());
    private Stage dialog;
    private TextArea detailsArea;

    public ConnectionErrorView() {
        LOGGER.fine("Creando ventana de error de conexión");
        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Error de Conexión");
        dialog.setMinWidth(400);
        dialog.setMinHeight(300);

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("error-dialog");

        Label messageLabel = new Label("No se pudo conectar al servidor");
        messageLabel.getStyleClass().add("error-message");

        detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(5);
        detailsArea.getStyleClass().add("error-details");
        setErrorDetails("Error de conexión: No se pudo establecer la conexión con el servidor.\n\n" +
                       "Posibles causas:\n" +
                       "1. El servidor no está en ejecución\n" +
                       "2. La dirección o puerto del servidor son incorrectos\n" +
                       "3. Hay un problema con la red\n\n" +
                       "Por favor, verifica que:\n" +
                       "1. El servidor esté ejecutándose\n" +
                       "2. El puerto configurado (12345) esté disponible\n" +
                       "3. No haya un firewall bloqueando la conexión");

        Button retryButton = new Button("Reintentar");
        retryButton.getStyleClass().add("retry-button");
        retryButton.setOnAction(e -> {
            LOGGER.info("Usuario solicitó reintentar conexión");
            dialog.close();
            App.mostrarLoginView();
        });

        layout.getChildren().addAll(messageLabel, detailsArea, retryButton);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/Styles/error-dialog.css").toExternalForm());
        dialog.setScene(scene);
        
        LOGGER.fine("Ventana de error de conexión creada exitosamente");
    }

    public void setErrorDetails(String details) {
        LOGGER.fine("Actualizando detalles del error: " + details);
        detailsArea.setText(details);
    }

    public void show() {
        LOGGER.info("Mostrando ventana de error de conexión");
        dialog.showAndWait();
    }
}
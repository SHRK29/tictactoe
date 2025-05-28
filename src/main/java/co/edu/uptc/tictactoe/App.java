package co.edu.uptc.tictactoe;
import co.edu.uptc.tictactoe.Network.ClientConnection;
import co.edu.uptc.tictactoe.Utils.WindowUtils;
import co.edu.uptc.tictactoe.Views.LoginView;



import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static ClientConnection clientConnection;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        initializeNetwork();
        mostrarLoginView();
    }

    private void initializeNetwork() {
        try {
            clientConnection = new ClientConnection();
            clientConnection.startConnection("localhost", 5555);

        } catch (Exception e) {
            System.out.println("No se ha podido realizar la conexión al servidor");
        }
    }

    public static void mostrarLoginView() {
        LoginView vistaLogin = new LoginView();
        cambiarEscena(vistaLogin.getScene(), "Tic Tac Toe - Login");
    }

    public static void cambiarEscena(Scene nuevaEscena, String titulo) {
        primaryStage.setScene(nuevaEscena);
        primaryStage.setTitle(titulo);
        WindowUtils.configurarVentanaPantallaCompleta(primaryStage);
        primaryStage.show();
    }

    public static ClientConnection getClientConnection() {
        return clientConnection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package main;

import view.ConnexionController;
import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Création unique du controller principal (qui contient la façade)
        Controller appController = new Controller();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connexion.fxml"));
        Parent root = loader.load();

        ConnexionController connexionController = loader.getController();
        connexionController.setAppController(appController);

        primaryStage.setTitle("RBFA - Connexion");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 
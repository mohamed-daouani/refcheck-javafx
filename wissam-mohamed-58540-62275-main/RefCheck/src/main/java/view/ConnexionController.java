package view;

import controller.Controller;
import database.dto.ArbitreDto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Optional;

public class ConnexionController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Controller appController;

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(this::handleLogin);
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleLogin(new ActionEvent());
            }
        });
    }

    /**
     * Handles the login attempt when the user clicks the login button or presses Enter.
     * Verifies the username and password, then navigates to the match list if successful.
     *
     * @param event the ActionEvent triggered by the button click.
     */
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText().trim();

        Optional<ArbitreDto> referee = appController.authentifier(username, password);

        if (referee.isPresent()) {
            appController.setArbitreCourant(referee.get());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listeMatchs.fxml"));
                Parent root = loader.load();

                ListeMatchsController listeController = loader.getController();
                listeController.setAppController(appController);
                listeController.setRefereeId(referee.get().matricule());

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("RBFA - Mes Matchs");
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            showAlert();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connexion");
        alert.setHeaderText("Échec de la connexion");
        alert.setContentText("Nom d'utilisateur ou mot de passe incorrect.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;-fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        ButtonType buttonTypeOk = alert.getButtonTypes().getFirst();
        Button okButton = (Button) dialogPane.lookupButton(buttonTypeOk);
        okButton.setStyle("-fx-background-color: #B39B63; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(179,155,99,0.3), 10, 0, 0, 2);");

        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: #B39B63; -fx-font-size: 16px; -fx-font-weight: bold;");

        alert.showAndWait();
    }
}

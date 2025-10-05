package view;

import controller.Controller;
import database.dto.MatchDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListeMatchsController {

    @FXML
    private VBox matchsContainer;
    @FXML
    private Text refereeName;
    @FXML
    private ImageView refereePhoto;
    @FXML
    private Button logoutButton;

    private Controller appController;
    private int refereeId;

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    public void setRefereeId(int refereeId) {
        this.refereeId = refereeId;
        loadRefereeInfo();
        loadMatchs();
    }

    @FXML
    public void initialize() {
        logoutButton.setOnAction(event -> handleLogout());
    }

    /**
     * Loads the current referee’s information and displays their name and photo.
     */
    private void loadRefereeInfo() {
        var arbitre = appController.getArbitreCourant();
        if (arbitre != null) {
            refereeName.setText(arbitre.prenom() + " " + arbitre.nom());
            if (arbitre.face_url() != null && !arbitre.face_url().isEmpty()) {
                try {
                    String photoPath = arbitre.face_url().replace("\"", "");
                    if (!photoPath.startsWith("images/faces/")) {
                        photoPath = "images/faces/" + photoPath;
                    }
                    String resourcePath = "/" + photoPath;
                    Image image = new Image(getClass().getResourceAsStream(resourcePath));
                    refereePhoto.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads all matches assigned to the current referee and creates match cards to display them in the UI.
     */
    private void loadMatchs() {
        matchsContainer.getChildren().clear();

        List<MatchDto> matchs = appController.getMatchsPourArbitre(refereeId);
        for (MatchDto match : matchs) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carteMatch.fxml"));
                VBox matchCard = loader.load();
                CarteMatchController cardController = loader.getController();
                cardController.setAppController(appController);
                cardController.setMatch(match);
                cardController.getMatchId().setText(String.valueOf(match.id()));

                String dateStr = match.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " " + match.heure().format(DateTimeFormatter.ofPattern("HH'h'mm"));
                cardController.getDateMatch().setText(dateStr + " @ ");

                cardController.getLieuMatch().setText(match.lieu());
                cardController.getEquipe1Nom().setText(match.clubANom());
                cardController.getEquipe2Nom().setText(match.clubBNom());

                if (match.clubALogoUrl() != null && !match.clubALogoUrl().isEmpty()) {
                    String logoPath = "/" + match.clubALogoUrl().replace("\"", "");
                    Image logoImage = new Image(getClass().getResourceAsStream(logoPath));
                    cardController.getEquipe1Logo().setImage(logoImage);
                }

                if (match.clubBLogoUrl() != null && !match.clubBLogoUrl().isEmpty()) {
                    String logoPath = "/" + match.clubBLogoUrl().replace("\"", "");
                    Image logoImage = new Image(getClass().getResourceAsStream(logoPath));
                    cardController.getEquipe2Logo().setImage(logoImage);
                }

                matchsContainer.getChildren().add(matchCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connexion.fxml"));
            Parent root = loader.load();
            ConnexionController connexionController = loader.getController();
            connexionController.setAppController(appController);
            appController.clearSession();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

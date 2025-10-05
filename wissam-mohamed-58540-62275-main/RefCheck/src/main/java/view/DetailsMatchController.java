package view;

import controller.Controller;
import database.dto.MatchDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class DetailsMatchController {
    @FXML
    private Label matchId;
    @FXML
    private Label dateMatch;
    @FXML
    private Label lieuMatch;
    @FXML
    private ImageView equipe1Logo;
    @FXML
    private ImageView equipe2Logo;
    @FXML
    private Label equipe1Nom;
    @FXML
    private Label equipe2Nom;
    @FXML
    private Button rognageEquipe1Button;
    @FXML
    private Button rognageEquipe2Button;
    @FXML
    private Text refereeName;
    @FXML
    private ImageView refereePhoto;
    @FXML
    private Button logoutButton;

    private MatchDto match;
    private Stage stage;
    private Controller appController;

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    public void setMatch(MatchDto match) {
        this.match = match;
        updateUI();
        loadRefereeInfo();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Loads and displays the referee’s information and photo for the current match.
     */
    private void loadRefereeInfo() {
        appController.chercherArbitre(match.arbitreMatricule()).ifPresent(arbitre -> {
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
        });
    }

    /**
     * Updates the match details on the UI, including date, location, team names, and team logos.
     */
    private void updateUI() {
        if (match != null) {
            matchId.setText(String.valueOf(match.id()));
            dateMatch.setText(match.date().toString() + " " + match.heure().toString() + " @ ");
            lieuMatch.setText(match.lieu());
            equipe1Nom.setText(match.clubANom());
            equipe2Nom.setText(match.clubBNom());

            rognageEquipe1Button.setText("IMPORTER LES PHOTOS DE " + match.clubANom());
            rognageEquipe2Button.setText("IMPORTER LES PHOTOS DE " + match.clubBNom());

            loadLogo(match.clubALogoUrl(), equipe1Logo);
            loadLogo(match.clubBLogoUrl(), equipe2Logo);
        }
    }

    /**
     * Loads and sets a team logo into the provided ImageView.
     *
     * @param url    the URL path to the logo image.
     * @param target the ImageView where the logo should be displayed.
     */
    private void loadLogo(String url, ImageView target) {
        try {
            if (url != null && !url.isEmpty()) {
                String logoPath = url.replace("\"", "");
                if (!logoPath.startsWith("/")) {
                    logoPath = "/" + logoPath;
                }
                Image logoImage = new Image(getClass().getResourceAsStream(logoPath));
                target.setImage(logoImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
        }
    }

    @FXML
    private void handleRognageEquipe1() {
        openCarteIdentite(match.clubAId(), match.clubANom());
    }

    @FXML
    private void handleRognageEquipe2() {
        openCarteIdentite(match.clubBId(), match.clubBNom());
    }

    /**
     * Opens the identity card import view for the specified team.
     *
     * @param equipeId  the ID of the team.
     * @param equipeNom the name of the team.
     */
    private void openCarteIdentite(int equipeId, String equipeNom) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carteIdentite.fxml"));
            Parent root = loader.load();
            CarteIdentiteController controller = loader.getController();
            controller.setAppController(appController);
            controller.setMatch(match, equipeId);
            controller.setStage(stage);

            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Importation des photos - " + equipeNom);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listeMatchs.fxml"));
            Parent root = loader.load();
            ListeMatchsController controller = loader.getController();
            controller.setAppController(appController);
            controller.setRefereeId(match.arbitreMatricule());

            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Mes Matchs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/connexion.fxml"));
            Parent root = loader.load();
            ConnexionController controller = loader.getController();
            controller.setAppController(appController);

            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Connexion");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

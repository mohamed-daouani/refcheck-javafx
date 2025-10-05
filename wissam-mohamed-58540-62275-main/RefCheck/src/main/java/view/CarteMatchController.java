package view;

import controller.Controller;
import database.dto.MatchDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class CarteMatchController {
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

    private MatchDto match;
    private Controller appController;

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    public void setMatch(MatchDto match) {
        this.match = match;
    }

    public Label getMatchId() {
        return matchId;
    }

    public Label getDateMatch() {
        return dateMatch;
    }

    public Label getLieuMatch() {
        return lieuMatch;
    }

    public ImageView getEquipe1Logo() {
        return equipe1Logo;
    }

    public ImageView getEquipe2Logo() {
        return equipe2Logo;
    }

    public Label getEquipe1Nom() {
        return equipe1Nom;
    }

    public Label getEquipe2Nom() {
        return equipe2Nom;
    }

    @FXML
    private void handleVoirMatch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/detailsMatch.fxml"));
            Parent root = loader.load();
            DetailsMatchController controller = loader.getController();
            controller.setAppController(appController);
            controller.setMatch(match);

            Stage stage = (Stage) matchId.getScene().getWindow();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Détails du match");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

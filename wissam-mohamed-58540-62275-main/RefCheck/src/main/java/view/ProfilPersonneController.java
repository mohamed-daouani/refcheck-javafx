package view;

import controller.Controller;
import database.dto.MembreClubDto;
import database.repository.MembreClubRepository;
import database.repository.ClubRepository;
import database.dto.MatchDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.OcrScanner;
import utils.CarteInfo;

import java.io.IOException;
import java.util.List;

public class ProfilPersonneController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label sexeLabel;
    @FXML
    private Label dateNaissanceLabel;
    @FXML
    private Label nationaliteLabel;
    @FXML
    private Label dateExpirationLabel;
    @FXML
    private Label numRegistreLabel;
    @FXML
    private Label numCarteLabel;

    @FXML
    private Label nomBdLabel;
    @FXML
    private Label prenomBdLabel;
    @FXML
    private Label dateNaissanceBdLabel;
    @FXML
    private Label roleBdLabel;
    @FXML
    private Label dateExpirationBdLabel;
    @FXML
    private Label statutAssociationLabel;
    @FXML
    private Label suspensionBdLabel;
    @FXML
    private Rectangle suspensionStatusRect;
    @FXML
    private Label clubNomBdLabel;
    @FXML
    private Button suivantButton;


    private int currentIndex = 0;
    private List<CarteInfo> ocrList;
    private final MembreClubRepository membreClubRepository = new MembreClubRepository();
    private int currentClubId;
    private MatchDto match;
    private Controller appController;

    public void initialize() {
        if (ocrList == null || ocrList.isEmpty()) {
            suivantButton.setDisable(true);
            return;
        }

        currentIndex = 0;
        afficherFicheCourante();
    }

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    /**
     * Displays the currently selected OCR profile data and compares it to the database record.
     */
    private void afficherFicheCourante() {
        ocrList = OcrScanner.getExtractedInfos();

        if (currentIndex >= ocrList.size()) {
            return;
        }

        CarteInfo fiche = ocrList.get(currentIndex);

        nomLabel.setText("Nom : " + fiche.getNom());
        prenomLabel.setText("Prénom : " + fiche.getPrenom());
        sexeLabel.setText("Sexe : " + fiche.getSexe());
        dateNaissanceLabel.setText("Date de naissance : " + fiche.getDateNaissance());
        nationaliteLabel.setText("Nationalité : " + fiche.getNationalite());
        dateExpirationLabel.setText("Date d'expiration : " + fiche.getDateExpiration());
        numRegistreLabel.setText("N° registre : " + fiche.getNumeroRegistre());
        numCarteLabel.setText("N° carte : " + fiche.getNumeroCarte());

        String numRegistreNettoye = fiche.getNumeroRegistre();
        if (numRegistreNettoye != null) {
            numRegistreNettoye = numRegistreNettoye.replaceAll("\\s", "").replaceAll("[^0-9]", "").trim();
        } else {
            numRegistreNettoye = "";
        }
        membreClubRepository.getByNumRegistre(numRegistreNettoye).ifPresentOrElse(
                membre -> {
                    nomBdLabel.setText("Nom : " + membre.getNom());
                    prenomBdLabel.setText("Prénom : " + membre.getPrenom());
                    dateNaissanceBdLabel.setText("Date de naissance : " + membre.getDateNaissance());
                    roleBdLabel.setText("Rôle : " + membre.getRole());
                    dateExpirationBdLabel.setText("Date d'expiration : " + membre.getDateExpiration());
                    suspensionBdLabel.setText("Suspension : ");
                    if (membre.getSuspension() == 0) {
                        suspensionStatusRect.setStyle("-fx-fill: #4CAF50; -fx-stroke: #333; -fx-stroke-width: 1.2;");
                    } else {
                        suspensionStatusRect.setStyle("-fx-fill: #E53935; -fx-stroke: #333; -fx-stroke-width: 1.2;");
                    }

                    try {
                        ClubRepository clubRepo = ClubRepository.getInstance();
                        clubRepo.getClubById(membre.getClubId())
                                .ifPresentOrElse(
                                        club -> clubNomBdLabel.setText("Club : " + club.nom()),
                                        () -> clubNomBdLabel.setText("Club : non trouvé")
                                );
                    } catch (Exception e) {
                        clubNomBdLabel.setText("Club : erreur");
                    }

                    boolean[] verifications = verifierInformations(fiche, membre);
                    String statut = genererStatutVerification(verifications);
                    statutAssociationLabel.setText("Statut : " + statut);

                    if (verifications[0] && verifications[1] && verifications[2] && verifications[3] && verifications[4]) {
                        statutAssociationLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        statutAssociationLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                },
                () -> {
                    nomBdLabel.setText("Nom : non trouvé");
                    prenomBdLabel.setText("Prénom : non trouvé");
                    dateNaissanceBdLabel.setText("Date de naissance : non trouvée");
                    roleBdLabel.setText("Rôle : -");
                    dateExpirationBdLabel.setText("Date d'expiration : -");
                    suspensionBdLabel.setText("Suspension : -");
                    suspensionStatusRect.setStyle("-fx-fill: #BDBDBD; -fx-stroke: #333; -fx-stroke-width: 1.2;");
                    clubNomBdLabel.setText("Club : -");
                    statutAssociationLabel.setText("Statut : Non associé");
                    statutAssociationLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
        );

        if (currentIndex == ocrList.size() - 1) {
            suivantButton.setText("Terminer");
        }
    }

    /**
     * Verifies the extracted OCR information against the club member’s database record.
     *
     * @param fiche  the CarteInfo object extracted via OCR.
     * @param membre the MembreClubDto object from the database.
     * @return a boolean array representing the verification status of each field.
     */
    private boolean[] verifierInformations(CarteInfo fiche, MembreClubDto membre) {
        boolean[] verifications = new boolean[6];
        verifications[0] = fiche.getNumeroRegistre().equals(membre.getNumRegistre());
        verifications[1] = fiche.getDateNaissance().equals(membre.getDateNaissance());
        verifications[2] = fiche.getDateExpiration().equals(membre.getDateExpiration());
        verifications[3] = fiche.getNom().equalsIgnoreCase(membre.getNom()) && fiche.getPrenom().equalsIgnoreCase(membre.getPrenom());
        verifications[4] = membre.getClubId() == currentClubId;
        verifications[5] = membre.getSuspension() == 0;
        return verifications;
    }

    /**
     * Generates a summary string describing the verification status.
     *
     * @param verifications the array of verification results.
     * @return a human-readable verification summary string.
     */
    private String genererStatutVerification(boolean[] verifications) {
        StringBuilder statut = new StringBuilder();
        if (verifications[0] && verifications[1] && verifications[2] && verifications[3] && verifications[4] && verifications[5]) {
            return "✅ Le joueur est apte à jouer";
        }

        if (!verifications[0]) statut.append("❌ N° registre ");
        if (!verifications[1]) statut.append("❌ Date naissance ");
        if (!verifications[2]) statut.append("❌ Date expiration ");
        if (!verifications[3]) statut.append("❌ Nom/Prénom ");
        if (!verifications[4]) statut.append("❌ Club incorrect ");
        if (!verifications[5]) statut.append("❌ Joueur Suspendu ");

        return statut.toString().replaceAll(", $", "");
    }

    @FXML
    private void onSuivantClicked() {
        if (suivantButton.getText().equals("Terminer")) {
            ouvrirVueFinale();
        } else {
            currentIndex++;
            afficherFicheCourante();
        }
    }

    private void ouvrirVueFinale() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tableauBord.fxml"));
            Parent root = loader.load();
            TableauBordController controller = loader.getController();
            controller.setAppController(appController);
            controller.setMatch(match);

            Stage stage = (Stage) suivantButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Tableau de bord");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Erreur lors de l'ouverture de la vue finale : " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ouverture de la vue finale");
            alert.setContentText("Une erreur est survenue lors de l'ouverture de la vue finale : " + e.getMessage());
            alert.showAndWait();
        }
    }

    public void setMatch(MatchDto match,int clubId) {
        this.match = match;
        this.currentClubId = clubId;

        OcrScanner.setCurrentMatch(match);
        ocrList = appController.getCartesAnalysees();

        if (!ocrList.isEmpty()) {
            currentIndex = 0;
            afficherFicheCourante();
            suivantButton.setDisable(false);
        } else {
            suivantButton.setDisable(true);
        }
    }
}

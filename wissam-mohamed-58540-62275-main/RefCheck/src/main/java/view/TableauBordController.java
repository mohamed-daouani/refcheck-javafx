package view;

import controller.Controller;
import database.dto.MatchDto;
import database.dto.MembreClubDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.OcrScanner;
import utils.CarteInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableauBordController {

    @FXML
    private TableView<JoueurInfo> equipe1Table;
    @FXML
    private TableView<JoueurInfo> equipe2Table;

    @FXML
    private TableColumn<JoueurInfo, String> nomCol1;
    @FXML
    private TableColumn<JoueurInfo, String> prenomCol1;
    @FXML
    private TableColumn<JoueurInfo, String> numRegistreCol1;
    @FXML
    private TableColumn<JoueurInfo, String> statutCol1;
    @FXML
    private TableColumn<JoueurInfo, String> raisonCol1;

    @FXML
    private TableColumn<JoueurInfo, String> nomCol2;
    @FXML
    private TableColumn<JoueurInfo, String> prenomCol2;
    @FXML
    private TableColumn<JoueurInfo, String> numRegistreCol2;
    @FXML
    private TableColumn<JoueurInfo, String> statutCol2;
    @FXML
    private TableColumn<JoueurInfo, String> raisonCol2;

    private Controller appController;
    private MatchDto match;
    private int equipe1Id;
    private int equipe2Id;

    public void setAppController(Controller appController) {
        this.appController = appController;
    }

    public void setMatch(MatchDto match) {
        this.match = match;
        if (match != null) {
            this.equipe1Id = match.clubAId();
            this.equipe2Id = match.clubBId();
        }
        chargerDonneesEquipes();
    }

    @FXML
    public void initialize() {
        nomCol1.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol1.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        numRegistreCol1.setCellValueFactory(new PropertyValueFactory<>("numRegistre"));
        statutCol1.setCellValueFactory(new PropertyValueFactory<>("statut"));
        raisonCol1.setCellValueFactory(new PropertyValueFactory<>("raison"));

        nomCol2.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol2.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        numRegistreCol2.setCellValueFactory(new PropertyValueFactory<>("numRegistre"));
        statutCol2.setCellValueFactory(new PropertyValueFactory<>("statut"));
        raisonCol2.setCellValueFactory(new PropertyValueFactory<>("raison"));
    }

    /**
     * Loads and populates the player data for both teams into their respective tables.
     */
    private void chargerDonneesEquipes() {
        List<CarteInfo> cartesVerifiees = appController.getCartesAnalysees();
        List<JoueurInfo> joueursEquipe1 = new ArrayList<>();
        List<JoueurInfo> joueursEquipe2 = new ArrayList<>();

        for (CarteInfo carte : cartesVerifiees) {
            int equipeCarte = carte.getEquipeId();
            String numRegistreNettoye = carte.getNumeroRegistre().replaceAll("\\s", "").replaceAll("[^0-9]", "").trim();

            if (carte.getNom().equals("Erreur")) {
                JoueurInfo joueur = new JoueurInfo(
                        "Scan non valide",
                        "Scan non valide",
                        carte.getNumeroRegistre(),
                        "Non valide",
                        "Scan non valide - Impossible de lire la carte"
                );
                if (equipeCarte == equipe1Id) {
                    joueursEquipe1.add(joueur);
                } else if (equipeCarte == equipe2Id) {
                    joueursEquipe2.add(joueur);
                }
                continue;
            }

            appController.chercherMembre(numRegistreNettoye).ifPresentOrElse(
                    membre -> {
                        boolean[] verifications = verifierInformations(carte, membre);
                        String raison = genererRaison(verifications);
                        JoueurInfo joueur = new JoueurInfo(
                                membre.getNom(),
                                membre.getPrenom(),
                                membre.getNumRegistre(),
                                verifierStatutJoueur(membre),
                                raison
                        );
                        if (membre.getClubId() == equipe1Id) {
                            joueursEquipe1.add(joueur);
                        } else if (membre.getClubId() == equipe2Id) {
                            joueursEquipe2.add(joueur);
                        } else {
                            joueur.setRaison("Joueur n'appartient pas à l'une des équipes du match");
                            if (equipeCarte == equipe1Id) {
                                joueursEquipe1.add(joueur);
                            } else if (equipeCarte == equipe2Id) {
                                joueursEquipe2.add(joueur);
                            }
                        }
                    },
                    () -> {
                        JoueurInfo joueur = new JoueurInfo(
                                carte.getNom(),
                                carte.getPrenom(),
                                carte.getNumeroRegistre(),
                                "Non trouvé",
                                "Joueur non trouvé dans la base de données"
                        );
                        if (equipeCarte == equipe1Id) {
                            joueursEquipe1.add(joueur);
                        } else if (equipeCarte == equipe2Id) {
                            joueursEquipe2.add(joueur);
                        }
                    }
            );
        }

        equipe1Table.getItems().setAll(joueursEquipe1);
        equipe2Table.getItems().setAll(joueursEquipe2);
    }

    /**
     * Verifies the consistency of OCR-extracted information against the member database record.
     *
     * @param fiche  the CarteInfo object from OCR.
     * @param membre the MembreClubDto object from the database.
     * @return a boolean array indicating which fields match.
     */
    private boolean[] verifierInformations(CarteInfo fiche, MembreClubDto membre) {
        boolean[] verifications = new boolean[5];
        verifications[0] = fiche.getNumeroRegistre().equals(membre.getNumRegistre());
        verifications[1] = fiche.getDateNaissance().equals(membre.getDateNaissance());
        verifications[2] = fiche.getDateExpiration().equals(membre.getDateExpiration());
        verifications[3] = fiche.getNom().equalsIgnoreCase(membre.getNom()) && fiche.getPrenom().equalsIgnoreCase(membre.getPrenom());
        verifications[4] = membre.getClubId() == equipe1Id || membre.getClubId() == equipe2Id;
        return verifications;
    }

    /**
     * Generates a descriptive string explaining any mismatches found in the verification.
     *
     * @param verifications the boolean array of field verifications.
     * @return a human-readable explanation string.
     */
    private String genererRaison(boolean[] verifications) {
        if (verifications[0] && verifications[1] && verifications[2] && verifications[3] && verifications[4]) {
            return "✅ Joueur validé";
        }

        StringBuilder raison = new StringBuilder();
        if (!verifications[0]) raison.append("N° registre incorrect, ");
        if (!verifications[1]) raison.append("Date de naissance incorrecte, ");
        if (!verifications[2]) raison.append("Date d'expiration incorrecte, ");
        if (!verifications[3]) raison.append("Nom/Prénom incorrect, ");
        if (!verifications[4]) raison.append("Club incorrect, ");

        return raison.toString().replaceAll(", $", "");
    }


    /**
     * Determines the playing status of a player based on suspension status.
     *
     * @param membre the MembreClubDto object.
     * @return "Suspendu" if the player is suspended, "Valide" otherwise.
     */
    private String verifierStatutJoueur(MembreClubDto membre) {
        return membre.getSuspension() > 0 ? "Suspendu" : "Valide";
    }

    @FXML
    private void rescanEquipe1() {
        ouvrirVueVerification(equipe1Id);
    }

    @FXML
    private void rescanEquipe2() {
        ouvrirVueVerification(equipe2Id);
    }

    /**
     * Opens the identity card verification view for the specified team.
     *
     * @param equipeId the ID of the team to rescan.
     */
    private void ouvrirVueVerification(int equipeId) {
        try {
            OcrScanner.clearScan();
            appController.initialiserOcrPourMatch(match, equipeId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/carteIdentite.fxml"));
            Parent root = loader.load();

            CarteIdentiteController controller = loader.getController();
            controller.setAppController(appController);
            controller.setMatch(match, equipeId);
            controller.setStage((Stage) equipe1Table.getScene().getWindow());

            Stage currentStage = (Stage) equipe1Table.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Vérification des cartes - Équipe " + equipeId);
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listeMatchs.fxml"));
            Parent root = loader.load();

            ListeMatchsController controller = loader.getController();
            controller.setAppController(appController);
            controller.setRefereeId(appController.getArbitreId());

            Stage stage = (Stage) equipe1Table.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RBFA - Liste des matchs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Data class representing a player's information for the dashboard table.
     */
    public static class JoueurInfo {
        private final String nom;
        private final String prenom;
        private final String numRegistre;
        private final String statut;
        private String raison;

        public JoueurInfo(String nom, String prenom, String numRegistre, String statut, String raison) {
            this.nom = nom;
            this.prenom = prenom;
            this.numRegistre = numRegistre;
            this.statut = statut;
            this.raison = raison;
        }

        public String getNom() {
            return nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public String getNumRegistre() {
            return numRegistre;
        }

        public String getStatut() {
            return statut;
        }

        public String getRaison() {
            return raison;
        }

        public void setRaison(String raison) {
            this.raison = raison;
        }
    }
}

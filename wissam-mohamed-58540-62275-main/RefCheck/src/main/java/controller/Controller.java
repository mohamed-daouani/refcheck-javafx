package controller;

import database.dto.ArbitreDto;
import database.dto.MatchDto;
import database.dto.MembreClubDto;
import model.AppFacade;
import utils.CarteInfo;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class Controller {

    private final AppFacade facade;

    public Controller() {
        this.facade = new AppFacade();
    }

    /**
     * Authenticates a referee using login and password.
     *
     * @param login    the login identifier of the referee
     * @param password the password of the referee
     * @return an Optional containing the authenticated ArbitreDto if successful, or empty if authentication fails
     */
    public Optional<ArbitreDto> authentifier(String login, String password) {
        return facade.login(login, password);
    }

    /**
     * Retrieves the ID of the currently authenticated referee.
     *
     * @return the referee's ID as an integer
     */
    public int getArbitreId() {
        return facade.getArbitreId();
    }

    /**
     * Launches the analysis process for a list of identity card images using OCR.
     *
     * @param cartes   a list of BufferedImage objects representing the cards
     * @param onFinish a Runnable callback to execute when the analysis is complete
     */
    public void lancerAnalyseCartes(List<BufferedImage> cartes, Runnable onFinish) {
        facade.traiterCartesIdentite(cartes, onFinish);
    }

    /**
     * Gets the list of card information extracted after OCR analysis.
     *
     * @return a list of CarteInfo objects containing extracted card details
     */
    public List<CarteInfo> getCartesAnalysees() {
        return facade.getCartesExtraites();
    }

    /**
     * Retrieves the list of matches assigned to a specific referee.
     *
     * @param id the ID of the referee
     * @return a list of MatchDto objects representing the matches
     */
    public List<MatchDto> getMatchsPourArbitre(int id) {
        return facade.getMatchsArbitre(id);
    }

    /**
     * Searches for a club member by their registration number.
     *
     * @param numRegistre the registration number of the member
     * @return an Optional containing the MembreClubDto if found, or empty if not found
     */
    public Optional<MembreClubDto> chercherMembre(String numRegistre) {
        return facade.getMembreByNumRegistre(numRegistre);
    }

    /**
     * Searches for an referee by their matriculation number.
     *
     * @param matricule the matriculation number of the referee
     * @return an Optional containing the ArbitreDto if found, or empty if not found
     */
    public Optional<ArbitreDto> chercherArbitre(int matricule) {
        return facade.getArbitreById(matricule);
    }

    /**
     * Initializes the OCR system for a specific match and team.
     *
     * @param match    the MatchDto representing the match
     * @param equipeId the ID of the team
     */
    public void initialiserOcrPourMatch(MatchDto match, int equipeId) {
        facade.initialiserOcrPourMatch(match, equipeId);
    }

    /**
     * Sets the current referee in the session.
     *
     * @param arbitre the ArbitreDto representing the referee
     */
    public void setArbitreCourant(ArbitreDto arbitre) {
        facade.setArbitreCourant(arbitre);
    }

    /**
     * Retrieves the currently active referee in the session.
     *
     * @return the ArbitreDto of the current referee
     */
    public ArbitreDto getArbitreCourant() {
        return facade.getArbitreCourant();
    }

    /**
     * Clears the current session, removing any active referee or match data.
     */
    public void clearSession() {
        facade.clearSession();
    }

}

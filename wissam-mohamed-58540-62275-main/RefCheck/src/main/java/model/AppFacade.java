package model;

import database.dto.ArbitreDto;
import database.dto.MatchDto;
import database.dto.MembreClubDto;
import database.repository.ArbitreRepository;
import database.repository.MembreClubRepository;
import utils.CarteInfo;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class AppFacade {

    private final ArbitreRepository arbitreRepo = ArbitreRepository.getInstance();
    private final MembreClubRepository membreRepo = new MembreClubRepository();


    /**
     * Attempts to log in a referee using the provided login and password.
     *
     * @param login    the login identifier
     * @param password the password
     * @return an Optional containing the ArbitreDto if authentication succeeds, or an empty Optional if it fails
     */
    public Optional<ArbitreDto> login(String login, String password) {
        ArbitreConnexion connexion = new ArbitreConnexion();
        return Optional.ofNullable(connexion.connexion(login, password));
    }

    /**
     * Retrieves the currently authenticated referee's ID.
     *
     * @return the referee's ID
     */
    public int getArbitreId() {
        return arbitreRepo.getRefereeId();
    }

    /**
     * Processes a list of identity card images using OCR in a multithreaded way.
     *
     * @param images   the list of BufferedImage objects representing the identity cards
     * @param onFinish a Runnable callback to execute when processing is complete
     */
    public void traiterCartesIdentite(List<BufferedImage> images, Runnable onFinish) {
        OcrScanner.extractCarteInfoMultiThread(images, onFinish);
    }

    /**
     * Retrieves the list of extracted identity card information.
     *
     * @return a list of CarteInfo objects representing extracted data
     */
    public List<CarteInfo> getCartesExtraites() {
        return OcrScanner.getExtractedInfos();
    }

    /**
     * Retrieves the list of matches assigned to a specific referee.
     *
     * @param id the referee's ID
     * @return a list of MatchDto objects representing the matches
     */
    public List<MatchDto> getMatchsArbitre(int id) {
        return arbitreRepo.getMatchesByRefereeId(id);
    }

    /**
     * Retrieves a club member by their national registration number.
     *
     * @param numRegistre the national registration number
     * @return an Optional containing the MembreClubDto if found, or an empty Optional if not found
     */
    public Optional<MembreClubDto> getMembreByNumRegistre(String numRegistre) {
        return membreRepo.getByNumRegistre(numRegistre);
    }

    /**
     * Retrieves a referee by their ID.
     *
     * @param id the referee's ID
     * @return an Optional containing the ArbitreDto if found, or an empty Optional if not found
     */
    public Optional<ArbitreDto> getArbitreById(int id) {
        return arbitreRepo.findById(id);
    }

    /**
     * Initializes the OCR system for a given match and team.
     *
     * @param match    the MatchDto object representing the match
     * @param equipeId the ID of the team
     */
    public void initialiserOcrPourMatch(MatchDto match, int equipeId) {
        OcrScanner.setCurrentMatch(match);
        OcrScanner.setEquipeActuelle(equipeId);
    }

    /**
     * Sets the currently active referee in the session.
     *
     * @param arbitre the ArbitreDto object to set as the current referee
     */
    public void setArbitreCourant(ArbitreDto arbitre) {
        ArbitreSession.getInstance().setArbitreCourant(arbitre);
    }

    /**
     * Retrieves the currently active referee from the session.
     *
     * @return the ArbitreDto object representing the current referee
     */
    public ArbitreDto getArbitreCourant() {
        return ArbitreSession.getInstance().getArbitreCourant();
    }

    /**
     * Clears the current session, removing the active referee.
     */
    public void clearSession() {
        ArbitreSession.getInstance().clearSession();
    }

}

package model;

import database.dto.ArbitreDto;
import database.repository.ArbitreRepository;
import utils.PasswordUtil;

import java.util.Optional;

public class ArbitreConnexion {


    ArbitreRepository repository = ArbitreRepository.getInstance();

    public ArbitreConnexion(ArbitreRepository repository) {
        this.repository = repository;
    }

    public ArbitreConnexion() {
        this.repository = ArbitreRepository.getInstance();
    }

    /**
     * Attempts to authenticate a referee using the provided login and password.
     *
     * @param login    the referee's login identifier
     * @param password the plain-text password provided by the user
     * @return the authenticated ArbitreDto if credentials are valid; null otherwise
     */
    public ArbitreDto connexion(String login, String password) {
        Optional<ArbitreDto> opt = repository.findByLogin(login);
        if (opt.isPresent()) {
            String storedHashedPassword = repository.getHashedPassword(opt.get().matricule());
            if (storedHashedPassword == null) {
                return null;
            }
                storedHashedPassword = storedHashedPassword.trim();
            if (PasswordUtil.checkPassword(password, storedHashedPassword)) {
                repository.setRefereeIdByLogin(login);
                return opt.get();
            }
        }

        return null;
    }
}

package database.repository;

import database.dao.ArbitreDao;
import database.dto.MatchDto;
import database.dto.ArbitreDto;

import java.util.List;
import java.util.Optional;

public class ArbitreRepository implements Repository<Integer, ArbitreDao> {

    private static ArbitreRepository instance;
    private final ArbitreDao arbitreDao;
    private int refereeId;


    private ArbitreRepository() {
        try {
            this.arbitreDao = new ArbitreDao();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du DAO", e);
        }
    }

    /**
     * Test constructor
     * @param arbitreDao for test
     */
    public ArbitreRepository(ArbitreDao arbitreDao) {
        this.arbitreDao = arbitreDao;
    }


    public static ArbitreRepository getInstance() {
        if (instance == null) {
            instance = new ArbitreRepository();
        }
        return instance;
    }


    public ArbitreDao getDao() {
        return arbitreDao;
    }

    @Override
    public Optional findById(Integer id) {
        return arbitreDao.findById(id);
    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public Integer save(ArbitreDao dto) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }

    public List<MatchDto> getMatchesByRefereeId(Integer id) {
        return arbitreDao.getMatchesByRefereeId(id);
    }

    public String getHashedPassword(int matricule) {
        return arbitreDao.getHashedPassword(matricule);
    }

    public Optional<ArbitreDto> findByLogin(String login) {
        return arbitreDao.findByLogin(login);
    }


    public void setRefereeIdByLogin(String login) {
        this.refereeId = findByLogin(login).get().matricule();
    }

    public int getRefereeId() {
        return refereeId;
    }
}

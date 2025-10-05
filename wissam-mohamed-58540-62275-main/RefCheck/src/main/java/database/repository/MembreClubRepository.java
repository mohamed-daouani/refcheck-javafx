package database.repository;

import database.dao.MembreClubDao;
import database.dto.MembreClubDto;

import java.util.List;
import java.util.Optional;

public class MembreClubRepository implements Repository<Integer, MembreClubDao> {

    private final MembreClubDao dao;

    public MembreClubRepository() {
        try {
            this.dao = new MembreClubDao();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du DAO", e);
        }
    }

    /**
     * Test constructor
     * @param dao for test
     */
    public MembreClubRepository(MembreClubDao dao) {
        this.dao = dao;
    }


    @Override
    public Optional<MembreClubDao> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<MembreClubDao> findAll() {
        return List.of();
    }

    @Override
    public Integer save(MembreClubDao dto) {
        return 0;
    }

    @Override
    public void deleteById(Integer id) {

    }

    public Optional<MembreClubDto> getByNumRegistre(String numRegistre) {
        return dao.findByNumRegistre(numRegistre);
    }
}

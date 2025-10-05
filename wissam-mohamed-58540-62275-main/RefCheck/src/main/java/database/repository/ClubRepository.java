package database.repository;

import database.dao.ClubDao;
import database.dto.ClubDto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClubRepository implements Repository<Integer, ClubDao> {
    private static ClubRepository instance;
    private final ClubDao clubDao;

    private ClubRepository() throws SQLException {
        this.clubDao = new ClubDao();
    }

    /**
     * Test constructor
     * @param clubDao for test
     */
    public ClubRepository(ClubDao clubDao) {
        this.clubDao = clubDao;
    }


    public static ClubRepository getInstance() {
        if (instance == null) {
            try {
                instance = new ClubRepository();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public Optional<ClubDao> findById(Integer id) {
        return Optional.of(clubDao);
    }

    @Override
    public List<ClubDao> findAll() {
        return List.of(clubDao);
    }

    @Override
    public Integer save(ClubDao dto) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {
    }

    public Optional<ClubDto> getClubById(int id) {
        return clubDao.findById(id);
    }
} 
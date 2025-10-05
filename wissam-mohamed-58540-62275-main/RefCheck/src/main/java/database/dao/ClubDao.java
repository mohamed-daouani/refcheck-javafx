package database.dao;

import database.DatabaseManager;
import database.dto.ClubDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubDao implements Dao<Integer, ClubDto> {

    private final Connection conn;

    /**
     * Constructeur pour initialiser la connexion à la base de données.
     *
     * @throws SQLException Si une erreur survient lors de l'établissement de la connexion.
     */
    public ClubDao() throws SQLException {
        this.conn = DatabaseManager.getConnection();
    }

    /**
     * Finds a club by its ID.
     *
     * @param id the ID of the club
     * @return an Optional containing the ClubDto if found, or an empty Optional if not found
     */
    @Override
    public Optional<ClubDto> findById(Integer id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Club WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new ClubDto(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("logo_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves all clubs from the database.
     *
     * @return a list of ClubDto objects representing all clubs
     */
    @Override
    public List<ClubDto> findAll() {
        List<ClubDto> clubs = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Club");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                clubs.add(new ClubDto(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("logo_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubs;
    }

    /**
     * Saves a club entity to the database.
     * (Currently not implemented as it's not required.)
     *
     * @param entity the ClubDto entity to save
     */
    @Override
    public void save(ClubDto entity) {
        // Non implémenté car ce n'est pas nécessaire.
    }

    /**
     * Deletes a club entity from the database by its ID.
     * (Currently not implemented as it's not required.)
     *
     * @param id the ID of the club to delete
     */
    @Override
    public void delete(Integer id) {
        // Non implémenté car ce n'est pas nécessaire.
    }
}

package database.dao;

import database.DatabaseManager;
import database.dto.ArbitreDto;
import database.dto.MatchDto;
import utils.PasswordUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArbitreDao implements Dao<Integer, ArbitreDto> {

    private final Connection conn;

    /**
     * Constructs a new ArbitreDao and initializes the database connection.
     *
     * @throws SQLException if the connection to the database fails
     */
    public ArbitreDao() throws SQLException {
        this.conn = DatabaseManager.getConnection();
    }

    /**
     * Test constructor
     * @param connection to the db test
     */
    public ArbitreDao(Connection connection) {
        this.conn = connection;
    }

    /**
     * Finds a referee by their matricule (ID).
     *
     * @param id the matricule (ID) of the referee
     * @return an Optional containing the ArbitreDto if found, or an empty Optional if not found
     */
    @Override
    public Optional<ArbitreDto> findById(Integer id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT * FROM Arbitre a
                        JOIN Personne p ON a.matricule = p.matricule
                        WHERE a.matricule = ?
                    """);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArbitreDto dto = new ArbitreDto(
                        rs.getInt("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        model.Role.valueOf(rs.getString("role").toUpperCase()),
                        LocalDate.parse(rs.getString("date_naissance")),
                        LocalDate.parse(rs.getString("date_expiration")),
                        rs.getBoolean("suspension"),
                        rs.getString("login"),
                        rs.getString("face_url")
                );
                return Optional.of(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves all referee from the database.
     *
     * @return a list containing all ArbitreDto objects from the database
     */
    @Override
    public List<ArbitreDto> findAll() {
        List<ArbitreDto> list = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT * FROM Arbitre a
                        JOIN Personne p ON a.matricule = p.matricule
                    """);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ArbitreDto(
                        rs.getInt("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        model.Role.valueOf(rs.getString("role").toUpperCase()),
                        LocalDate.parse(rs.getString("date_naissance")),
                        LocalDate.parse(rs.getString("date_expiration")),
                        rs.getBoolean("suspension"),
                        rs.getString("login"),
                        rs.getString("photo_url")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Saves a referee into the database.
     * The data is inserted into both the Personne and Arbitre tables.
     * A default password is generated and hashed before being stored.
     *
     * @param r the ArbitreDto object to save
     */
    @Override
    public void save(ArbitreDto r) {
        try {
            // 1. Insérer dans la table Personne
            PreparedStatement ps = conn.prepareStatement("""
                        INSERT OR REPLACE INTO Personne
                        (matricule, nom, prenom, role, date_naissance, date_expiration, suspension)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    """);
            ps.setInt(1, r.matricule());
            ps.setString(2, r.nom());
            ps.setString(3, r.prenom());
            ps.setString(4, r.role().toString().toLowerCase());
            ps.setString(5, String.valueOf(r.dateNaissance()));
            ps.setString(6, String.valueOf(r.dateExpiration()));
            ps.setBoolean(7, r.suspension());
            ps.executeUpdate();

            // 2. Insérer dans la table Arbitre (utiliser un mot de passe par défaut haché)
            PreparedStatement aStmt = conn.prepareStatement("""
                        INSERT OR REPLACE INTO Arbitre (matricule, login, password)
                        VALUES (?, ?, ?)
                    """);
            aStmt.setInt(1, r.matricule());
            aStmt.setString(2, r.login());

            String hashed = PasswordUtil.hashPassword("password123"); // Mot de passe par défaut
            aStmt.setString(3, hashed);

            aStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a referee from the database by their matricule (ID).
     * This method removes data from both the Arbitre and Personne tables.
     *
     * @param id the matricule (ID) of the referee to delete
     */
    @Override
    public void delete(Integer id) {
        try {
            PreparedStatement s1 = conn.prepareStatement("DELETE FROM Arbitre WHERE matricule = ?");
            s1.setInt(1, id);
            s1.executeUpdate();

            PreparedStatement s2 = conn.prepareStatement("DELETE FROM Personne WHERE matricule = ?");
            s2.setInt(1, id);
            s2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all matches assigned to a referee using their matricule (ID).
     *
     * @param id the matricule (ID) of the referee
     * @return a list of MatchDto objects representing the referee's matches
     */
    public List<MatchDto> getMatchesByRefereeId(Integer id) {
        List<MatchDto> list = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT m.*, c1.nom as club_a_nom, c2.nom as club_b_nom, c1.logo_url as club_a_logo, c2.logo_url as club_b_logo
                        FROM Match m
                        JOIN Club c1 ON m.club_a_id = c1.id
                        JOIN Club c2 ON m.club_b_id = c2.id
                        WHERE m.arbitre_matricule = ?
                    """);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                LocalTime heure = LocalTime.parse(rs.getString("heure"));

                list.add(new MatchDto(
                        rs.getInt("id"),
                        rs.getInt("club_a_id"),
                        rs.getInt("club_b_id"),
                        rs.getString("lieu"),
                        date,
                        heure,
                        rs.getString("score"),
                        rs.getInt("arbitre_matricule"),
                        rs.getString("club_a_nom"),
                        rs.getString("club_b_nom"),
                        rs.getString("club_a_logo"),
                        rs.getString("club_b_logo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Finds a referee by their login.
     *
     * @param login the login name of the referee
     * @return an Optional containing the ArbitreDto if found, or an empty Optional if not found
     */
    public Optional<ArbitreDto> findByLogin(String login) {
        try {
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT * FROM Arbitre a
                        JOIN Personne p ON a.matricule = p.matricule
                        WHERE a.login = ?
                    """);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ArbitreDto dto = new ArbitreDto(
                        rs.getInt("matricule"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        model.Role.valueOf(rs.getString("role").toUpperCase()),
                        LocalDate.parse(rs.getString("date_naissance")),
                        LocalDate.parse(rs.getString("date_expiration")),
                        rs.getBoolean("suspension"),
                        rs.getString("login"),
                        rs.getString("face_url")
                );
                return Optional.of(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Retrieves the hashed password of a referee by their matricule (ID).
     *
     * @param matricule the matricule (ID) of the referee
     * @return the hashed password string if found, or null if not found
     */
    public String getHashedPassword(int matricule) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT password FROM Arbitre WHERE matricule = ?"
            );
            stmt.setInt(1, matricule);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

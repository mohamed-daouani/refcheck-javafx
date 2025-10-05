package database.dao;

import database.DatabaseManager;
import database.dto.MembreClubDto;
import model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembreClubDao implements Dao<Integer, MembreClubDto> {

    private final Connection conn;

    /**
     * Constructeur principal utilisant DatabaseManager.
     */
    public MembreClubDao() {
        this.conn = DatabaseManager.getConnection();
    }

    /**
     * Constructeur alternatif pour les tests avec injection de connexion.
     */
    public MembreClubDao(Connection connection) {
        this.conn = connection;
    }

    @Override
    public Optional<MembreClubDto> findById(Integer id) {
        // Non implémenté
        return Optional.empty();
    }

    @Override
    public List<MembreClubDto> findAll() {
        // Non implémenté
        return new ArrayList<>();
    }

    @Override
    public void save(MembreClubDto entity) {
        // Non implémenté
    }

    @Override
    public void delete(Integer id) {
        // Non implémenté
    }

    /**
     * Recherche un membre de club par numéro de registre national.
     *
     * @param numRegistre numéro de registre national
     * @return membre trouvé ou vide
     */
    public Optional<MembreClubDto> findByNumRegistre(String numRegistre) {
        String sql = """
                SELECT
                    p.nom,
                    p.prenom,
                    p.date_naissance,
                    p.date_expiration,
                    p.role,
                    p.numero_registre_national,
                    mc.club_id,
                    p.suspension
                FROM MembreClub mc
                JOIN Personne p ON p.matricule = mc.matricule
                WHERE p.numero_registre_national = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numRegistre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new MembreClubDto(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        Role.valueOf(rs.getString("role").toUpperCase()),
                        rs.getString("date_naissance"),
                        rs.getString("date_expiration"),
                        rs.getString("numero_registre_national"),
                        rs.getInt("club_id"),
                        rs.getInt("suspension")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}

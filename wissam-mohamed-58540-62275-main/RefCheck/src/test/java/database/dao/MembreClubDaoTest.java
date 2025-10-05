package database.dao;

import database.dto.MembreClubDto;
import model.Role;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MembreClubDaoTest {

    private static Connection connection;
    private MembreClubDao dao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE Personne (
                    matricule INTEGER PRIMARY KEY,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    role TEXT NOT NULL,
                    date_naissance TEXT NOT NULL,
                    date_expiration TEXT NOT NULL,
                    suspension INTEGER NOT NULL,
                    numero_registre_national TEXT NOT NULL UNIQUE,
                    club_id INTEGER
                );
            """);

            stmt.execute("""
                CREATE TABLE MembreClub (
                    matricule INTEGER PRIMARY KEY,
                    club_id INTEGER NOT NULL
                );
            """);
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        dao = new MembreClubDao(connection);

        try (PreparedStatement ps1 = connection.prepareStatement("""
            INSERT INTO Personne (
                matricule, nom, prenom, role, date_naissance, date_expiration, 
                suspension, numero_registre_national, club_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """);
             PreparedStatement ps2 = connection.prepareStatement("""
            INSERT INTO MembreClub (matricule, club_id) VALUES (?, ?)
        """)) {

            ps1.setInt(1, 1);
            ps1.setString(2, "Durand");
            ps1.setString(3, "Luc");
            ps1.setString(4, "joueur");
            ps1.setString(5, "2000-01-01");
            ps1.setString(6, "2030-01-01");
            ps1.setInt(7, 0); // suspension false
            ps1.setString(8, "12345678901");
            ps1.setInt(9, 42);
            ps1.executeUpdate();

            ps2.setInt(1, 1);
            ps2.setInt(2, 42);
            ps2.executeUpdate();
        }
    }

    @AfterEach
    void clean() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM MembreClub");
            stmt.execute("DELETE FROM Personne");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByNumRegistreExists() {
        Optional<MembreClubDto> result = dao.findByNumRegistre("12345678901");

        assertTrue(result.isPresent());
        assertEquals("Durand", result.get().getNom());
        assertEquals("Luc", result.get().getPrenom());
        assertEquals("JOUEUR", result.get().getRole());
    }

    @Test
    void testFindByNumRegistreNotFound() {
        Optional<MembreClubDto> result = dao.findByNumRegistre("00000000000");
        assertTrue(result.isEmpty());
    }
}

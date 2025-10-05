package database.dao;

import database.dto.ArbitreDto;
import model.Role;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArbitreDaoTest {

    private static Connection connection;
    private ArbitreDao dao;

    private final ArbitreDto arbitreTest = new ArbitreDto(
            1, "Dupont", "Jean", Role.ARBITRE,
            LocalDate.of(1985, 4, 12),
            LocalDate.of(2030, 12, 31),
            false, "ref01", "face.jpg"
    );

    @BeforeAll
    static void initDB() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE Personne (
                            matricule INTEGER PRIMARY KEY,
                            nom TEXT NOT NULL,
                            prenom TEXT NOT NULL,
                            role TEXT NOT NULL,
                            date_naissance DATE NOT NULL,
                            date_expiration DATE NOT NULL,
                            suspension BOOLEAN DEFAULT FALSE,
                            face_url TEXT,
                            photo_url TEXT,
                            numero_registre_national TEXT NOT NULL DEFAULT '00000000001',
                            club_id INTEGER
                        );
                    """);
            stmt.execute("""
                    CREATE TABLE Arbitre (
                        matricule INTEGER PRIMARY KEY,
                        login TEXT NOT NULL,
                        password TEXT NOT NULL
                    );
                    """);
        }
    }


    @BeforeEach
    void setup() {
        dao = new ArbitreDao(connection);
        dao.save(arbitreTest);
    }

    @AfterEach
    void clean() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Arbitre");
            stmt.execute("DELETE FROM Personne");
        }
    }

    @AfterAll
    static void closeDB() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByIdExists() {
        Optional<ArbitreDto> found = dao.findById(1);
        assertTrue(found.isPresent());
        assertEquals(arbitreTest.nom(), found.get().nom());
    }

    @Test
    void testFindByIdDoesNotExist() {
        Optional<ArbitreDto> found = dao.findById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByLoginExists() {
        Optional<ArbitreDto> found = dao.findByLogin("ref01");
        assertTrue(found.isPresent());
        assertEquals("Dupont", found.get().nom());
    }

    @Test
    void testFindByLoginNotFound() {
        Optional<ArbitreDto> found = dao.findByLogin("inexistant");
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAll() {
        List<ArbitreDto> list = dao.findAll();
        assertEquals(1, list.size());
        assertEquals("ref01", list.get(0).login());
    }

    @Test
    void testDelete() {
        dao.delete(1);
        assertTrue(dao.findById(1).isEmpty());
    }
}

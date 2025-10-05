package database.dao;

import database.dto.ClubDto;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClubDaoTest {

    private static Connection connection;
    private ClubDao dao;

    private final ClubDto testClub = new ClubDto(1, "FC Test", "logo.png");

    @BeforeAll
    static void initDb() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE Club (
                    id INTEGER PRIMARY KEY,
                    nom TEXT NOT NULL,
                    logo_url TEXT
                );
            """);
        }
    }

    @BeforeEach
    void setup() throws SQLException, NoSuchFieldException, IllegalAccessException {
        dao = new ClubDao() {
            {
                // Injection manuelle de la connexion
                var field = ClubDao.class.getDeclaredField("conn");
                field.setAccessible(true);
                field.set(this, connection);
            }
        };

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Club (id, nom, logo_url) VALUES (?, ?, ?)"
        )) {
            ps.setInt(1, testClub.id());
            ps.setString(2, testClub.nom());
            ps.setString(3, testClub.logoUrl());
            ps.executeUpdate();
        }
    }

    @AfterEach
    void cleanDb() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM Club");
        }
    }

    @AfterAll
    static void closeDb() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByIdExists() {
        Optional<ClubDto> found = dao.findById(1);
        assertTrue(found.isPresent());
        assertEquals("FC Test", found.get().nom());
    }

    @Test
    void testFindByIdNotExists() {
        Optional<ClubDto> found = dao.findById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAll() {
        List<ClubDto> all = dao.findAll();
        assertEquals(1, all.size());
        assertEquals("FC Test", all.get(0).nom());
    }
}

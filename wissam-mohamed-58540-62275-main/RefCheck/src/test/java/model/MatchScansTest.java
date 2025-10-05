package model;

import database.dto.MatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CarteInfo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchScansTest {

    private MatchDto matchA;
    private MatchDto matchB;
    private CarteInfo carte1;
    private CarteInfo carte2;

    @BeforeEach
    void setUp() {
        // Création de deux matchs distincts
        matchA = new MatchDto(1, 101, 102, "Stade A",
                LocalDate.of(2024, 5, 10), LocalTime.of(15, 0),
                "0-0", 1001, "Club A", "Club B", "logoA.png", "logoB.png");
        matchB = new MatchDto(2, 103, 104, "Stade B",
                LocalDate.of(2024, 5, 11), LocalTime.of(16, 0),
                "0-0", 1002, "Club C", "Club D", "logoC.png", "logoD.png");

        // Création de deux cartes fictives
        carte1 = new CarteInfo("Nom1", "Prenom1", "2000-01-01", "FR", "M", "2030-01-01", "123456", "CARTE1");
        carte2 = new CarteInfo("Nom2", "Prenom2", "1999-05-05", "FR", "F", "2030-05-05", "654321", "CARTE2");

        // Nettoyage à chaque test pour éviter les interférences
        MatchScans.clearScans(matchA);
        MatchScans.clearScans(matchB);
    }

    @Test
    void testAjouterEtRecupererScans() {
        MatchScans.ajouterScan(matchA, carte1);
        MatchScans.ajouterScan(matchA, carte2);

        List<CarteInfo> scans = MatchScans.getScans(matchA);
        assertEquals(2, scans.size(), "Il doit y avoir deux scans pour matchA");
        assertTrue(scans.contains(carte1), "Le scan carte1 doit être présent");
        assertTrue(scans.contains(carte2), "Le scan carte2 doit être présent");
    }

    @Test
    void testIsolationDesMatchs() {
        MatchScans.ajouterScan(matchA, carte1);
        MatchScans.ajouterScan(matchB, carte2);

        List<CarteInfo> scansA = MatchScans.getScans(matchA);
        List<CarteInfo> scansB = MatchScans.getScans(matchB);

        assertEquals(1, scansA.size(), "MatchA doit avoir 1 scan");
        assertEquals(1, scansB.size(), "MatchB doit avoir 1 scan");
        assertTrue(scansA.contains(carte1), "MatchA doit contenir carte1");
        assertTrue(scansB.contains(carte2), "MatchB doit contenir carte2");
    }

    @Test
    void testEquipeActuelleParDefaut() {
        int equipeId = MatchScans.getEquipeActuelle(matchA);
        assertEquals(1, equipeId, "Par défaut, l’équipe actuelle doit être 1");
    }

    @Test
    void testSetEtGetEquipeActuelle() {
        MatchScans.setEquipeActuelle(matchA, 42);
        int equipeId = MatchScans.getEquipeActuelle(matchA);
        assertEquals(42, equipeId, "L’équipe actuelle doit être mise à jour correctement");
    }

    @Test
    void testClearScans() {
        MatchScans.ajouterScan(matchA, carte1);
        MatchScans.setEquipeActuelle(matchA, 42);

        MatchScans.clearScans(matchA);

        List<CarteInfo> scans = MatchScans.getScans(matchA);
        int equipeId = MatchScans.getEquipeActuelle(matchA);

        assertTrue(scans.isEmpty(), "Après clearScans, la liste doit être vide");
        assertEquals(1, equipeId, "Après clearScans, l’équipe actuelle doit revenir à 1");
    }
}

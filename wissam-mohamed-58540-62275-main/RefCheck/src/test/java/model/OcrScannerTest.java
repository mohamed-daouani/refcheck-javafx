package model;

import database.dto.MatchDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CarteInfo;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class OcrScannerTest {

    private MatchDto match;

    @BeforeEach
    void setUp() {
        match = new MatchDto(1, 101, 102, "Stade A",
                LocalDate.of(2024, 5, 10), LocalTime.of(15, 0),
                "0-0", 1001, "Club A", "Club B", "logoA.png", "logoB.png");

        OcrScanner.setCurrentMatch(match);
        OcrScanner.clearScan();  // Toujours repartir propre
    }

    @Test
    void testSetAndGetEquipeActuelle() {
        assertEquals(-1, OcrScanner.getEquipeActuelle(), "Sans match actif, doit retourner -1");

        OcrScanner.setCurrentMatch(match);
        assertEquals(1, OcrScanner.getEquipeActuelle(), "Par défaut, l’équipe doit être 1");

        OcrScanner.setEquipeActuelle(42);
        assertEquals(42, OcrScanner.getEquipeActuelle(), "Après set, l’équipe doit être mise à jour");
    }

    @Test
    void testClearScan() {
        OcrScanner.setEquipeActuelle(55);
        OcrScanner.clearScan();

        assertEquals(-1, OcrScanner.getEquipeActuelle(), "Après clear, sans match actif, doit être -1");
        assertTrue(OcrScanner.getExtractedInfos().isEmpty(), "Après clear, les scans doivent être vides");
    }

    @Test
    void testExtractInfoFromMRZ() {
        String fakeMrz = """
            IDFRA123456789<123
            9502286F3001060FRA95022899874
            DOE<<JOHN<<<<<<<<<<<<<<<<<
            """;

        CarteInfo carte = OcrScanner.extractInfo(fakeMrz);

        assertEquals("DOE", carte.getNom());
        assertEquals("JOHN", carte.getPrenom());
        assertEquals("2095-02-28", carte.getDateNaissance());
        assertEquals("F", carte.getSexe());
        assertEquals("2030-01-06", carte.getDateExpiration(), "La date d’expiration doit être formatée");
        assertEquals("FRA", carte.getNationalite());
        assertEquals("95022899874", carte.getNumeroRegistre());
        assertEquals("123456789123", carte.getNumeroCarte());  // IDFRA123456789<<123 → prend substrings pour carte
    }


    @Test
    void testExtractInfoWithInvalidFormatThrows() {
        String invalidText = "INVALID TEXT WITHOUT 3 LINES";

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> OcrScanner.extractInfo(invalidText),
                "Un format invalide doit déclencher une exception");
        assertTrue(exception.getMessage().contains("Format invalide"), "Le message doit mentionner 'Format invalide'");
    }

    @Test
    void testCalculateAgeFromValidDate() {
        String date = "01/01/2000";
        int age = OcrScanner.calculateAgeFromDate(date);
        int expectedAge = LocalDate.now().getYear() - 2000;
        assertEquals(expectedAge, age, "L’âge doit être correctement calculé");
    }

    @Test
    void testCalculateAgeFromInvalidDate() {
        String date = "INVALID";
        int age = OcrScanner.calculateAgeFromDate(date);
        assertEquals(0, age, "En cas d’erreur, l’âge doit être 0");
    }
}

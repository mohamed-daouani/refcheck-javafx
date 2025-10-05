package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarteInfoTest {

    @Test
    void testConstructorWithMRZ() {
        CarteInfo carte = new CarteInfo(
                "Doe", "John", "1990-01-01", "Belge", "M",
                "2030-12-31", "123456789", "987654321"
        );

        assertEquals("Doe", carte.getNom());
        assertEquals("John", carte.getPrenom());
        assertEquals("1990-01-01", carte.getDateNaissance());
        assertEquals("Belge", carte.getNationalite());
        assertEquals("M", carte.getSexe());
        assertEquals("2030-12-31", carte.getDateExpiration());
        assertEquals("123456789", carte.getNumeroRegistre());
        assertEquals("987654321", carte.getNumeroCarte());
        assertEquals(-1, carte.getEquipeId()); // valeur par défaut
    }

    @Test
    void testConstructorWithRegex() {
        CarteInfo carte = new CarteInfo(
                "Doe", "Jane", "1992-05-10", "123 Rue Exemple", 32
        );

        assertEquals("Doe", carte.getNom());
        assertEquals("Jane", carte.getPrenom());
        assertEquals("1992-05-10", carte.getDateNaissance());
        assertEquals("", carte.getNationalite());
        assertEquals("", carte.getSexe());
        assertEquals("", carte.getDateExpiration());
        assertEquals("123 Rue Exemple", carte.getNumeroRegistre()); // adresse stockée ici
        assertEquals("32", carte.getNumeroCarte()); // âge stocké ici
        assertEquals(-1, carte.getEquipeId());
    }

    @Test
    void testEquipeIdSetterGetter() {
        CarteInfo carte = new CarteInfo(
                "Doe", "Test", "2000-01-01", "Belge", "F",
                "2030-12-31", "987654321", "123456789"
        );

        assertEquals(-1, carte.getEquipeId()); // avant modification

        carte.setEquipeId(5);
        assertEquals(5, carte.getEquipeId());

        carte.setEquipeId(0);
        assertEquals(0, carte.getEquipeId());

        carte.setEquipeId(-10);
        assertEquals(-10, carte.getEquipeId());
    }

    @Test
    void testToStringContent() {
        CarteInfo carte = new CarteInfo(
                "Doe", "John", "1990-01-01", "Belge", "M",
                "2030-12-31", "123456789", "987654321"
        );

        String output = carte.toString();

        assertTrue(output.contains("Nom : Doe"));
        assertTrue(output.contains("Prénom : John"));
        assertTrue(output.contains("Sexe : M"));
        assertTrue(output.contains("Date de naissance : 1990-01-01"));
        assertTrue(output.contains("Nationalité : Belge"));
        assertTrue(output.contains("Date d'expiration : 2030-12-31"));
        assertTrue(output.contains("N° registre : 123456789"));
        assertTrue(output.contains("N° carte : 987654321"));
    }
}

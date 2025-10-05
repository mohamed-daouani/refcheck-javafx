package model;

import database.dto.ArbitreDto;
import database.repository.ArbitreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.PasswordUtil;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArbitreConnexionTest {

    private ArbitreRepository repositoryMock;
    private ArbitreConnexion arbitreConnexion;

    @BeforeEach
    void setUp() {
        repositoryMock = mock(ArbitreRepository.class);
        arbitreConnexion = new ArbitreConnexion(repositoryMock);
    }

    @Test
    void testConnexionSuccess() {
        String login = "ref123";
        String plainPassword = "password";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        ArbitreDto arbitre = new ArbitreDto(
                1, "Dupont", "Jean", Role.ARBITRE,
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2030, 12, 31),
                false, login, "face.jpg"
        );

        when(repositoryMock.findByLogin(login)).thenReturn(Optional.of(arbitre));
        when(repositoryMock.getHashedPassword(arbitre.matricule())).thenReturn(hashedPassword);

        ArbitreDto result = arbitreConnexion.connexion(login, plainPassword);

        assertNotNull(result, "Successful login should return the ArbitreDto");
        assertEquals(arbitre, result, "Returned ArbitreDto should match the repository result");
        verify(repositoryMock).setRefereeIdByLogin(login);
    }

    @Test
    void testConnexionWrongPassword() {
        String login = "ref123";
        String wrongPassword = "wrong";
        String hashedPassword = PasswordUtil.hashPassword("correct");

        ArbitreDto arbitre = new ArbitreDto(
                1, "Dupont", "Jean", Role.ARBITRE,
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2030, 12, 31),
                false, login, "face.jpg"
        );

        when(repositoryMock.findByLogin(login)).thenReturn(Optional.of(arbitre));
        when(repositoryMock.getHashedPassword(arbitre.matricule())).thenReturn(hashedPassword);

        ArbitreDto result = arbitreConnexion.connexion(login, wrongPassword);

        assertNull(result, "Wrong password should return null");
        verify(repositoryMock, never()).setRefereeIdByLogin(any());
    }

    @Test
    void testConnexionNonExistingLogin() {
        String login = "unknown";

        when(repositoryMock.findByLogin(login)).thenReturn(Optional.empty());

        ArbitreDto result = arbitreConnexion.connexion(login, "any");

        assertNull(result, "Non-existing login should return null");
        verify(repositoryMock, never()).getHashedPassword(anyInt());
        verify(repositoryMock, never()).setRefereeIdByLogin(any());
    }

    @Test
    void testConnexionWithNullStoredPassword() {
        String login = "ref123";

        ArbitreDto arbitre = new ArbitreDto(
                1, "Dupont", "Jean", Role.ARBITRE,
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2030, 12, 31),
                false, login, "face.jpg"
        );

        when(repositoryMock.findByLogin(login)).thenReturn(Optional.of(arbitre));
        when(repositoryMock.getHashedPassword(arbitre.matricule())).thenReturn(null);

        ArbitreDto result = arbitreConnexion.connexion(login, "any");

        assertNull(result, "Null stored password should return null");
        verify(repositoryMock, never()).setRefereeIdByLogin(any());
    }
}

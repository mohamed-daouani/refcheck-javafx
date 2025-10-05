package database.repository;

import database.dao.MembreClubDao;
import database.dto.MembreClubDto;
import model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MembreClubRepositoryTest {

    private MembreClubDao membreDao;
    private MembreClubRepository repository;
    private final MembreClubDto membre = new MembreClubDto(
            "Durand", "Luc", Role.JOUEUR,
            "2000-01-01", "2030-01-01", "12345678901", 42, 0
    );

    @BeforeEach
    void setup() {
        membreDao = mock(MembreClubDao.class);
        repository = new MembreClubRepository(membreDao);
    }

    @Test
    void testGetByNumRegistreExists() {
        when(membreDao.findByNumRegistre("12345678901")).thenReturn(Optional.of(membre));

        Optional<MembreClubDto> result = repository.getByNumRegistre("12345678901");

        assertTrue(result.isPresent());
        assertEquals(membre, result.get());
        verify(membreDao).findByNumRegistre("12345678901");
    }

    @Test
    void testGetByNumRegistreNotFound() {
        when(membreDao.findByNumRegistre("00000000000")).thenReturn(Optional.empty());

        Optional<MembreClubDto> result = repository.getByNumRegistre("00000000000");

        assertFalse(result.isPresent());
        verify(membreDao).findByNumRegistre("00000000000");
    }
}

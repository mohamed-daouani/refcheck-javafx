package database.repository;

import database.dao.ArbitreDao;
import database.dto.ArbitreDto;
import model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArbitreRepositoryTest {

    private ArbitreDao arbitreDao;
    private ArbitreRepository repository;

    private final ArbitreDto arbitre = new ArbitreDto(
            1, "Test", "Arbitre", Role.ARBITRE,
            LocalDate.of(1990, 1, 1),
            LocalDate.of(2030, 1, 1),
            false, "login01", "photo.jpg"
    );

    @BeforeEach
    void setup() {
        arbitreDao = mock(ArbitreDao.class);
        repository = new ArbitreRepository(arbitreDao);
    }

    @Test
    void testFindByIdExists() {
        when(arbitreDao.findById(1)).thenReturn(Optional.of(arbitre));

        Optional<ArbitreDto> result = repository.findById(1);

        assertTrue(result.isPresent());
        assertEquals(arbitre, result.get());
        verify(arbitreDao, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(arbitreDao.findById(99)).thenReturn(Optional.empty());

        Optional<ArbitreDto> result = repository.findById(99);

        assertFalse(result.isPresent());
        verify(arbitreDao).findById(99);
    }

    @Test
    void testFindByLogin() {
        when(arbitreDao.findByLogin("login01")).thenReturn(Optional.of(arbitre));

        Optional<ArbitreDto> result = repository.findByLogin("login01");

        assertTrue(result.isPresent());
        assertEquals(arbitre, result.get());
        verify(arbitreDao).findByLogin("login01");
    }
    @Test
    void testSetRefereeIdByLoginWhenAbsent() {
        when(arbitreDao.findByLogin("absent")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            repository.setRefereeIdByLogin("absent");
        });
    }

}

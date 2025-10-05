package database.repository;

import database.dao.ClubDao;
import database.dto.ClubDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClubRepositoryTest {

    private ClubDao clubDao;
    private ClubRepository repository;
    private final ClubDto club = new ClubDto(1, "FC Test", "logo.png");

    @BeforeEach
    void setup() {
        clubDao = mock(ClubDao.class);
        repository = new ClubRepository(clubDao);
    }

    @Test
    void testGetClubByIdExists() {
        when(clubDao.findById(1)).thenReturn(Optional.of(club));

        Optional<ClubDto> result = repository.getClubById(1);

        assertTrue(result.isPresent());
        assertEquals(club, result.get());
        verify(clubDao).findById(1);
    }

    @Test
    void testGetClubByIdNotFound() {
        when(clubDao.findById(2)).thenReturn(Optional.empty());

        Optional<ClubDto> result = repository.getClubById(2);

        assertFalse(result.isPresent());
        verify(clubDao).findById(2);
    }
}

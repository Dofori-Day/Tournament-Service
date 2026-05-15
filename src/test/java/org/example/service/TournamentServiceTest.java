package org.example.service;

import org.example.dto.TournamentCardDTO;
import org.example.model.Tournament;
import org.example.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {

    @Mock
    private TournamentRepository repository;

    private TournamentService service;

    @BeforeEach
    void setUp() {
        service = new TournamentService(repository);
    }

    @Test
    void createTournament_asOrganizer_success() {
        when(repository.isOrganizer(1L)).thenReturn(true);
        Tournament tournament = new Tournament();
        tournament.setTitle("Test Tournament");

        service.createTournament(1L, tournament);

        assertEquals(1L, tournament.getCreatedBy());
        verify(repository).createTournament(tournament);
    }

    @Test
    void createTournament_notOrganizer_throws() {
        when(repository.isOrganizer(2L)).thenReturn(false);
        Tournament tournament = new Tournament();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createTournament(2L, tournament));
        assertEquals("User is not an organizer", ex.getMessage());
        verify(repository, never()).createTournament(any());
    }

    @Test
    void closeSubmission_asOrganizer_updatesStatus() {
        when(repository.isOrganizer(1L)).thenReturn(true);

        service.closeSubmission(1L, 100L);

        verify(repository).updateTournamentStatus(100L, "SUBMISSION_CLOSED");
    }

    @Test
    void closeSubmission_notOrganizer_throws() {
        when(repository.isOrganizer(2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.closeSubmission(2L, 100L));
        verify(repository, never()).updateTournamentStatus(any(), any());
    }

    @Test
    void startEvaluation_asOrganizer_updatesStatus() {
        when(repository.isOrganizer(1L)).thenReturn(true);

        service.startEvaluation(1L, 200L);

        verify(repository).updateTournamentStatus(200L, "EVALUATION");
    }

    @Test
    void startEvaluation_notOrganizer_throws() {
        when(repository.isOrganizer(2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.startEvaluation(2L, 200L));
    }

    @Test
    void finishTournament_asOrganizer_updatesStatus() {
        when(repository.isOrganizer(1L)).thenReturn(true);

        service.finishTournament(1L, 300L);

        verify(repository).updateTournamentStatus(300L, "FINISHED");
    }

    @Test
    void finishTournament_notOrganizer_throws() {
        when(repository.isOrganizer(2L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.finishTournament(2L, 300L));
    }

    @Test
    void getTournaments_withStatus_delegates() {
        TournamentCardDTO dto = new TournamentCardDTO();
        dto.setId(1L);
        when(repository.getTournaments("ACTIVE")).thenReturn(List.of(dto));

        List<TournamentCardDTO> result = service.getTournaments("ACTIVE");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getTournaments_withNullStatus_delegates() {
        when(repository.getTournaments(null)).thenReturn(List.of());

        List<TournamentCardDTO> result = service.getTournaments(null);

        assertTrue(result.isEmpty());
        verify(repository).getTournaments(null);
    }
}

package org.example.service;

import org.example.dto.CreateRoundRequest;
import org.example.model.Round;
import org.example.repository.RoundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoundServiceTest {

    @Mock
    private RoundRepository roundRepository;

    private RoundService roundService;

    @BeforeEach
    void setUp() {
        roundService = new RoundService(roundRepository);
    }

    @Test
    void createRound_setsStatusDraftAndSaves() {
        CreateRoundRequest request = new CreateRoundRequest();
        request.setTournamentId(1L);
        request.setTitle("Round 1");
        request.setDescription("First round");
        request.setRoundOrder(1);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusDays(1));

        roundService.createRound(request);

        verify(roundRepository).save(argThat(round ->
                "DRAFT".equals(round.getStatus()) &&
                "Round 1".equals(round.getTitle()) &&
                1L == round.getTournamentId() &&
                1 == round.getRoundOrder()
        ));
    }

    @Test
    void getRound_returnsRound() {
        Round round = new Round();
        round.setId(1L);
        when(roundRepository.findById(1L)).thenReturn(round);

        Round result = roundService.getRound(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getRound_notFound_returnsNull() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertNull(roundService.getRound(999L));
    }

    @Test
    void getRoundsByTournament_delegates() {
        when(roundRepository.findByTournamentId(1L)).thenReturn(List.of(new Round()));

        List<Round> result = roundService.getRoundsByTournament(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAllRounds_delegates() {
        when(roundRepository.findAll()).thenReturn(List.of(new Round(), new Round()));

        List<Round> result = roundService.getAllRounds();

        assertEquals(2, result.size());
    }

    @Test
    void getActiveRounds_delegates() {
        when(roundRepository.findActive()).thenReturn(List.of());

        List<Round> result = roundService.getActiveRounds();

        assertTrue(result.isEmpty());
    }

    @Test
    void activate_roundFound_updatesStatus() {
        Round round = new Round();
        round.setId(1L);
        when(roundRepository.findById(1L)).thenReturn(round);

        roundService.activate(1L);

        verify(roundRepository).updateStatus(1L, "ACTIVE");
    }

    @Test
    void activate_roundNotFound_throws() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> roundService.activate(999L));
        verify(roundRepository, never()).updateStatus(any(), any());
    }

    @Test
    void closeSubmissions_roundFound_updatesStatus() {
        Round round = new Round();
        round.setId(1L);
        when(roundRepository.findById(1L)).thenReturn(round);

        roundService.closeSubmissions(1L);

        verify(roundRepository).updateStatus(1L, "SUBMISSION_CLOSED");
    }

    @Test
    void closeSubmissions_roundNotFound_throws() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> roundService.closeSubmissions(999L));
    }

    @Test
    void markEvaluated_roundFound_updatesStatus() {
        Round round = new Round();
        round.setId(1L);
        when(roundRepository.findById(1L)).thenReturn(round);

        roundService.markEvaluated(1L);

        verify(roundRepository).updateStatus(1L, "EVALUATED");
    }

    @Test
    void markEvaluated_roundNotFound_throws() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> roundService.markEvaluated(999L));
    }

    @Test
    void updateRound_roundFound_updates() {
        Round round = new Round();
        round.setId(1L);
        when(roundRepository.findById(1L)).thenReturn(round);

        CreateRoundRequest request = new CreateRoundRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated desc");
        request.setRoundOrder(2);
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusDays(2));

        roundService.updateRound(1L, request);

        assertEquals("Updated Title", round.getTitle());
        assertEquals("Updated desc", round.getDescription());
        assertEquals(2, round.getRoundOrder());
        verify(roundRepository).update(round);
    }

    @Test
    void updateRound_roundNotFound_throws() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> roundService.updateRound(999L, new CreateRoundRequest()));
        verify(roundRepository, never()).update(any());
    }
}

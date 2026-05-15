package org.example.service;

import org.example.dto.LeaderboardRowDTO;
import org.example.repository.LeaderboardRepository;
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
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;
    @Mock
    private TournamentRepository tournamentRepository;

    private LeaderboardService leaderboardService;

    @BeforeEach
    void setUp() {
        leaderboardService = new LeaderboardService(leaderboardRepository, tournamentRepository);
    }

    @Test
    void getLeaderboard_tournamentFinished_returnsList() {
        when(tournamentRepository.getTournamentStatus(1L)).thenReturn("FINISHED");
        LeaderboardRowDTO row = new LeaderboardRowDTO();
        row.setTeamId(1L);
        row.setTeamName("Team A");
        row.setTotalScore(85.5);
        when(leaderboardRepository.getLeaderboard(1L)).thenReturn(List.of(row));

        List<LeaderboardRowDTO> result = leaderboardService.getLeaderboard(1L);

        assertEquals(1, result.size());
        assertEquals("Team A", result.get(0).getTeamName());
        assertEquals(85.5, result.get(0).getTotalScore());
    }

    @Test
    void getLeaderboard_tournamentNotFinished_throws() {
        when(tournamentRepository.getTournamentStatus(1L)).thenReturn("EVALUATION");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> leaderboardService.getLeaderboard(1L));
        assertEquals("Leaderboard is not available yet", ex.getMessage());
        verify(leaderboardRepository, never()).getLeaderboard(any());
    }

    @Test
    void getLeaderboard_tournamentInSubmissionClosed_throws() {
        when(tournamentRepository.getTournamentStatus(1L)).thenReturn("SUBMISSION_CLOSED");

        assertThrows(RuntimeException.class, () -> leaderboardService.getLeaderboard(1L));
    }

    @Test
    void getLeaderboard_tournamentInRegistration_throws() {
        when(tournamentRepository.getTournamentStatus(1L)).thenReturn("REGISTRATION");

        assertThrows(RuntimeException.class, () -> leaderboardService.getLeaderboard(1L));
    }
}

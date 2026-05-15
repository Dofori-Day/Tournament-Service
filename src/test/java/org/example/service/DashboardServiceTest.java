package org.example.service;

import org.example.dto.UserDashboardDTO;
import org.example.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardRepository repository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(repository);
    }

    @Test
    void getDashboard_returnsDto() {
        UserDashboardDTO dto = new UserDashboardDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team X");
        dto.setTournamentId(10L);
        dto.setTournamentTitle("Tournament 1");
        dto.setTournamentStatus("ACTIVE");

        when(repository.getUserDashboard(1L)).thenReturn(dto);

        UserDashboardDTO result = dashboardService.getDashboard(1L);

        assertEquals(1L, result.getTeamId());
        assertEquals("Team X", result.getTeamName());
        assertEquals("ACTIVE", result.getTournamentStatus());
    }
}

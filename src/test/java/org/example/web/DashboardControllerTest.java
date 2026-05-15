package org.example.web;

import org.example.dto.UserDashboardDTO;
import org.example.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService service;

    @Test
    void getDashboard_returnsDto() throws Exception {
        UserDashboardDTO dto = new UserDashboardDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team X");
        dto.setTournamentId(10L);
        dto.setTournamentTitle("Tournament 1");
        dto.setTournamentStatus("ACTIVE");

        when(service.getDashboard(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(1))
                .andExpect(jsonPath("$.teamName").value("Team X"))
                .andExpect(jsonPath("$.tournamentStatus").value("ACTIVE"));
    }
}

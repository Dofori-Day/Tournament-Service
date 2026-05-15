package org.example.web;

import org.example.dto.LeaderboardRowDTO;
import org.example.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderboardService leaderboardService;

    @Test
    void getLeaderboard_returnsList() throws Exception {
        LeaderboardRowDTO row = new LeaderboardRowDTO();
        row.setTeamId(1L);
        row.setTeamName("Team A");
        row.setTotalScore(90.0);

        when(leaderboardService.getLeaderboard(1L)).thenReturn(List.of(row));

        mockMvc.perform(get("/api/tournaments/1/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(1))
                .andExpect(jsonPath("$[0].teamName").value("Team A"))
                .andExpect(jsonPath("$[0].totalScore").value(90.0));
    }

    @Test
    void getLeaderboard_empty() throws Exception {
        when(leaderboardService.getLeaderboard(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/tournaments/1/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

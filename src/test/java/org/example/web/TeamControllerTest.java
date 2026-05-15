package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateTeamRequest;
import org.example.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createTeam_returnsTeamId() throws Exception {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Team Awesome");

        when(teamService.createTeam(any(CreateTeamRequest.class))).thenReturn(42L);

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }
}

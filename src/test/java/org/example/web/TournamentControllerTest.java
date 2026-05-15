package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TournamentCardDTO;
import org.example.model.Tournament;
import org.example.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
@AutoConfigureMockMvc(addFilters = false)
class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createTournament_returnsOk() throws Exception {
        Tournament tournament = new Tournament();
        tournament.setTitle("Test");

        mockMvc.perform(post("/api/tournaments/create")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tournament)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tournament created"));

        verify(service).createTournament(eq(1L), any(Tournament.class));
    }

    @Test
    void closeSubmission_returnsOk() throws Exception {
        mockMvc.perform(put("/api/tournaments/1/close-submission")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Submission closed"));

        verify(service).closeSubmission(1L, 1L);
    }

    @Test
    void startEvaluation_returnsOk() throws Exception {
        mockMvc.perform(put("/api/tournaments/1/start-evaluation")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Evaluation started"));

        verify(service).startEvaluation(1L, 1L);
    }

    @Test
    void finishTournament_returnsOk() throws Exception {
        mockMvc.perform(put("/api/tournaments/1/finish")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Tournament finished"));

        verify(service).finishTournament(1L, 1L);
    }

    @Test
    void getTournaments_withStatus_returnsList() throws Exception {
        TournamentCardDTO dto = new TournamentCardDTO();
        dto.setId(1L);
        dto.setTitle("T1");
        when(service.getTournaments("ACTIVE")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/tournaments/get-tournaments")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("T1"));
    }

    @Test
    void getTournaments_withoutStatus_returnsList() throws Exception {
        when(service.getTournaments(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/tournaments/get-tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

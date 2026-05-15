package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateRoundRequest;
import org.example.model.Round;
import org.example.service.RoundService;
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

@WebMvcTest(RoundController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoundService roundService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void createRound_returnsOk() throws Exception {
        CreateRoundRequest request = new CreateRoundRequest();
        request.setTitle("Round 1");

        mockMvc.perform(post("/api/rounds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Round created"));

        verify(roundService).createRound(any(CreateRoundRequest.class));
    }

    @Test
    void getAll_returnsList() throws Exception {
        when(roundService.getAllRounds()).thenReturn(List.of(new Round(), new Round()));

        mockMvc.perform(get("/api/rounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getActive_returnsList() throws Exception {
        when(roundService.getActiveRounds()).thenReturn(List.of());

        mockMvc.perform(get("/api/rounds/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getByTournament_returnsList() throws Exception {
        when(roundService.getRoundsByTournament(1L)).thenReturn(List.of(new Round()));

        mockMvc.perform(get("/api/rounds/tournament/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRound_found() throws Exception {
        Round round = new Round();
        round.setId(1L);
        round.setTitle("Round 1");
        when(roundService.getRound(1L)).thenReturn(round);

        mockMvc.perform(get("/api/rounds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Round 1"));
    }

    @Test
    void getRound_notFound() throws Exception {
        when(roundService.getRound(999L)).thenReturn(null);

        mockMvc.perform(get("/api/rounds/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void activate_returnsOk() throws Exception {
        mockMvc.perform(put("/api/rounds/1/activate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Round activated"));
        verify(roundService).activate(1L);
    }

    @Test
    void close_returnsOk() throws Exception {
        mockMvc.perform(put("/api/rounds/1/close"))
                .andExpect(status().isOk())
                .andExpect(content().string("Submissions closed"));
        verify(roundService).closeSubmissions(1L);
    }

    @Test
    void evaluated_returnsOk() throws Exception {
        mockMvc.perform(put("/api/rounds/1/evaluated"))
                .andExpect(status().isOk())
                .andExpect(content().string("Round marked as evaluated"));
        verify(roundService).markEvaluated(1L);
    }

    @Test
    void update_returnsOk() throws Exception {
        CreateRoundRequest request = new CreateRoundRequest();
        request.setTitle("Updated");

        mockMvc.perform(put("/api/rounds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Round updated"));

        verify(roundService).updateRound(eq(1L), any(CreateRoundRequest.class));
    }
}

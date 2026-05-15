package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DistributeRequest;
import org.example.dto.EvaluationRequest;
import org.example.model.Evaluation;
import org.example.service.EvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationService evaluationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void distribute_success() throws Exception {
        DistributeRequest request = new DistributeRequest();
        request.setRoundId(1L);

        mockMvc.perform(post("/api/evaluations/distribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Distribution completed"));

        verify(evaluationService).distribute(any(DistributeRequest.class));
    }

    @Test
    void distribute_error() throws Exception {
        DistributeRequest request = new DistributeRequest();
        doThrow(new RuntimeException("No jurors available"))
                .when(evaluationService).distribute(any(DistributeRequest.class));

        mockMvc.perform(post("/api/evaluations/distribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No jurors available"));
    }

    @Test
    void save_returnsOk() throws Exception {
        EvaluationRequest request = new EvaluationRequest();
        request.setSubmissionId(1L);
        request.setJuryId(10L);

        mockMvc.perform(post("/api/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Evaluation saved"));

        verify(evaluationService).saveEvaluation(any(EvaluationRequest.class));
    }

    @Test
    void getBySubmission_returnsList() throws Exception {
        when(evaluationService.getEvaluationsBySubmission(1L)).thenReturn(List.of(new Evaluation()));

        mockMvc.perform(get("/api/evaluations/submission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getMy_returnsList() throws Exception {
        when(evaluationService.getMyEvaluations(10L)).thenReturn(List.of());

        mockMvc.perform(get("/api/evaluations/my/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getMyAssignments_returnsList() throws Exception {
        when(evaluationService.getMyAssignedSubmissionIds(10L)).thenReturn(List.of(1L, 2L));

        mockMvc.perform(get("/api/evaluations/my-assignments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
    }

    @Test
    void getAverage_returnsMap() throws Exception {
        when(evaluationService.getAverageScoresByRound(1L)).thenReturn(Map.of(1L, 85.5));

        mockMvc.perform(get("/api/evaluations/average/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['1']").value(85.5));
    }

    @Test
    void getOne_found() throws Exception {
        Evaluation e = new Evaluation();
        e.setSubmissionId(1L);
        e.setJuryId(10L);
        e.setBackendQuality(8);
        e.setEvaluatedAt(LocalDateTime.now());

        when(evaluationService.getEvaluation(1L, 10L)).thenReturn(e);

        mockMvc.perform(get("/api/evaluations/submission/1/jury/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(1))
                .andExpect(jsonPath("$.juryId").value(10))
                .andExpect(jsonPath("$.backendQuality").value(8));
    }

    @Test
    void getOne_notFound() throws Exception {
        when(evaluationService.getEvaluation(999L, 999L)).thenReturn(null);

        mockMvc.perform(get("/api/evaluations/submission/999/jury/999"))
                .andExpect(status().isNotFound());
    }
}

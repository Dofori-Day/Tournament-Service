package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.SubmissionRequest;
import org.example.model.Submission;
import org.example.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubmissionService submissionService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void create_success() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        request.setRoundId(1L);
        request.setTeamId(10L);

        when(submissionService.createSubmission(any(SubmissionRequest.class))).thenReturn(100L);

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void create_error() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        when(submissionService.createSubmission(any(SubmissionRequest.class)))
                .thenThrow(new RuntimeException("Round not found"));

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Round not found"));
    }

    @Test
    void update_success() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubLink("https://github.com/new");

        mockMvc.perform(put("/api/submissions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Submission updated"));

        verify(submissionService).updateSubmission(eq(1L), any(SubmissionRequest.class));
    }

    @Test
    void update_error() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        doThrow(new RuntimeException("Submission not found"))
                .when(submissionService).updateSubmission(eq(999L), any(SubmissionRequest.class));

        mockMvc.perform(put("/api/submissions/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Submission not found"));
    }

    @Test
    void get_found() throws Exception {
        Submission s = new Submission();
        s.setId(1L);
        s.setRoundId(1L);
        s.setTeamId(10L);
        s.setStatus("SUBMITTED");
        s.setSubmittedAt(LocalDateTime.now());

        when(submissionService.getSubmission(1L)).thenReturn(s);

        mockMvc.perform(get("/api/submissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roundId").value(1))
                .andExpect(jsonPath("$.teamId").value(10));
    }

    @Test
    void get_notFound() throws Exception {
        when(submissionService.getSubmission(999L)).thenReturn(null);

        mockMvc.perform(get("/api/submissions/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByRound() throws Exception {
        when(submissionService.getSubmissionsByRound(1L)).thenReturn(List.of(new Submission()));

        mockMvc.perform(get("/api/submissions/round/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getByTeam() throws Exception {
        when(submissionService.getSubmissionsByTeam(10L)).thenReturn(List.of());

        mockMvc.perform(get("/api/submissions/team/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

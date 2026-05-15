package org.example.web;

import org.example.dto.EvaluationExportDto;
import org.example.service.EvaluationExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationExportController.class)
@AutoConfigureMockMvc(addFilters = false)
class EvaluationExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluationExportService exportService;

    @Test
    void exportEvaluations_returnsCsv() throws Exception {
        EvaluationExportDto dto = new EvaluationExportDto(
                1L, "John Doe", 8, 7, 9, 6, 8, 7, 7.5, "Good", LocalDateTime.of(2025, 1, 15, 10, 0)
        );

        when(exportService.getExportData()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/export/evaluations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=evaluations.csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("John Doe")));
    }

    @Test
    void exportEvaluations_empty_returnsCsvWithHeader() throws Exception {
        when(exportService.getExportData()).thenReturn(List.of());

        mockMvc.perform(get("/api/export/evaluations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Submission ID")));
    }
}

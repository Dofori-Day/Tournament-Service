package org.example.service;

import org.example.dto.EvaluationExportDto;
import org.example.model.Evaluation;
import org.example.repository.EvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationExportServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    private EvaluationExportService exportService;

    @BeforeEach
    void setUp() {
        exportService = new EvaluationExportService(evaluationRepository);
    }

    @Test
    void getExportData_mapsAllFields() {
        Evaluation e = new Evaluation();
        e.setSubmissionId(1L);
        e.setJuryName("John Doe");
        e.setBackendQuality(8);
        e.setDatabaseScore(7);
        e.setFrontendQuality(9);
        e.setFunctionalityScore(6);
        e.setUsabilityScore(8);
        e.setMustHaveCompleteness(7);
        e.setComment("Nice");
        e.setEvaluatedAt(LocalDateTime.of(2025, 1, 15, 10, 0));

        when(evaluationRepository.findAll()).thenReturn(List.of(e));

        List<EvaluationExportDto> result = exportService.getExportData();

        assertEquals(1, result.size());
        EvaluationExportDto dto = result.get(0);
        assertEquals(1L, dto.submissionId());
        assertEquals("John Doe", dto.juryName());
        assertEquals(8, dto.backendQuality());
        assertEquals(7, dto.databaseScore());
        assertEquals(9, dto.frontendQuality());
        assertEquals(6, dto.functionalityScore());
        assertEquals(8, dto.usabilityScore());
        assertEquals(7, dto.mustHaveCompleteness());
        assertEquals("Nice", dto.comment());
        assertNotNull(dto.evaluatedAt());
    }

    @Test
    void getExportData_handlesNullScores() {
        Evaluation e = new Evaluation();
        e.setSubmissionId(1L);
        e.setJuryName("Jane");
        e.setComment("");

        when(evaluationRepository.findAll()).thenReturn(List.of(e));

        List<EvaluationExportDto> result = exportService.getExportData();

        assertEquals(1, result.size());
        EvaluationExportDto dto = result.get(0);
        assertNull(dto.backendQuality());
        assertNull(dto.databaseScore());
        assertEquals(0.0, dto.totalScore(), 0.001);
    }

    @Test
    void getExportData_emptyList() {
        when(evaluationRepository.findAll()).thenReturn(List.of());

        List<EvaluationExportDto> result = exportService.getExportData();

        assertTrue(result.isEmpty());
    }

    @Test
    void getExportData_multipleEvaluations() {
        Evaluation e1 = new Evaluation();
        e1.setSubmissionId(1L);
        e1.setJuryName("Jury 1");
        e1.setBackendQuality(10);

        Evaluation e2 = new Evaluation();
        e2.setSubmissionId(2L);
        e2.setJuryName("Jury 2");
        e2.setBackendQuality(8);

        when(evaluationRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EvaluationExportDto> result = exportService.getExportData();

        assertEquals(2, result.size());
        assertEquals("Jury 1", result.get(0).juryName());
        assertEquals("Jury 2", result.get(1).juryName());
    }
}

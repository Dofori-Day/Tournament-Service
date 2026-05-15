package org.example.service;

import org.example.dto.DistributeRequest;
import org.example.dto.EvaluationRequest;
import org.example.model.Evaluation;
import org.example.model.Round;
import org.example.model.Submission;
import org.example.model.User;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private JuryAssignmentRepository juryAssignmentRepository;
    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private RoundRepository roundRepository;
    @Mock
    private UserRepository userRepository;

    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationService = new EvaluationService(
                evaluationRepository, juryAssignmentRepository,
                submissionRepository, roundRepository, userRepository);
    }

    @Test
    void distribute_success() {
        DistributeRequest request = new DistributeRequest();
        request.setRoundId(1L);
        request.setEvaluationsPerSubmission(2);
        request.setMaxSubmissionsPerJuror(5);

        Round round = new Round();
        round.setId(1L);
        round.setStatus("SUBMISSION_CLOSED");

        Submission sub1 = new Submission(); sub1.setId(10L);
        Submission sub2 = new Submission(); sub2.setId(20L);
        User jury1 = new User(); jury1.setId(100L);
        User jury2 = new User(); jury2.setId(200L);

        when(roundRepository.findById(1L)).thenReturn(round);
        when(submissionRepository.findByRoundId(1L)).thenReturn(List.of(sub1, sub2));
        when(userRepository.findAllByRole("JURY")).thenReturn(List.of(jury1, jury2));
        when(juryAssignmentRepository.findJuryIdsBySubmissionId(anyLong()))
                .thenReturn(List.of());

        evaluationService.distribute(request);

        verify(juryAssignmentRepository).clearByRoundId(1L);
        verify(juryAssignmentRepository, atLeast(4)).assign(anyLong(), anyLong());
    }

    @Test
    void distribute_roundNotFound_throws() {
        when(roundRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> evaluationService.distribute(new DistributeRequest()));
    }

    @Test
    void distribute_roundNotClosed_throws() {
        Round round = new Round();
        round.setId(1L);
        round.setStatus("ACTIVE");
        when(roundRepository.findById(1L)).thenReturn(round);

        DistributeRequest request = new DistributeRequest();
        request.setRoundId(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> evaluationService.distribute(request));
        assertEquals("Submissions must be closed before distribution", ex.getMessage());
    }

    @Test
    void distribute_noSubmissions_throws() {
        Round round = new Round();
        round.setId(1L);
        round.setStatus("SUBMISSION_CLOSED");
        when(roundRepository.findById(1L)).thenReturn(round);
        when(submissionRepository.findByRoundId(1L)).thenReturn(List.of());

        DistributeRequest request = new DistributeRequest();
        request.setRoundId(1L);

        assertThrows(RuntimeException.class, () -> evaluationService.distribute(request));
    }

    @Test
    void distribute_noJurors_throws() {
        Round round = new Round();
        round.setId(1L);
        round.setStatus("SUBMISSION_CLOSED");
        when(roundRepository.findById(1L)).thenReturn(round);
        when(submissionRepository.findByRoundId(1L)).thenReturn(List.of(new Submission()));
        when(userRepository.findAllByRole("JURY")).thenReturn(List.of());

        DistributeRequest request = new DistributeRequest();
        request.setRoundId(1L);

        assertThrows(RuntimeException.class, () -> evaluationService.distribute(request));
    }

    @Test
    void saveEvaluation_savesCorrectly() {
        EvaluationRequest request = new EvaluationRequest();
        request.setSubmissionId(1L);
        request.setJuryId(10L);
        request.setBackendQuality(8);
        request.setDatabaseScore(7);
        request.setFrontendQuality(9);
        request.setFunctionalityScore(6);
        request.setUsabilityScore(8);
        request.setMustHaveCompleteness(7);
        request.setComment("Good work");

        evaluationService.saveEvaluation(request);

        verify(evaluationRepository).saveOrUpdate(argThat(e ->
                e.getSubmissionId() == 1L &&
                e.getJuryId() == 10L &&
                e.getBackendQuality() == 8 &&
                e.getDatabaseScore() == 7 &&
                "Good work".equals(e.getComment())
        ));
    }

    @Test
    void getEvaluation_returnsEvaluation() {
        Evaluation e = new Evaluation();
        e.setSubmissionId(1L);
        e.setJuryId(10L);
        when(evaluationRepository.findBySubmissionAndJury(1L, 10L)).thenReturn(e);

        Evaluation result = evaluationService.getEvaluation(1L, 10L);

        assertNotNull(result);
        assertEquals(1L, result.getSubmissionId());
    }

    @Test
    void getEvaluation_notFound_returnsNull() {
        when(evaluationRepository.findBySubmissionAndJury(999L, 999L)).thenReturn(null);

        assertNull(evaluationService.getEvaluation(999L, 999L));
    }

    @Test
    void getEvaluationsBySubmission_returnsList() {
        when(evaluationRepository.findBySubmissionId(1L)).thenReturn(List.of(new Evaluation()));

        assertEquals(1, evaluationService.getEvaluationsBySubmission(1L).size());
    }

    @Test
    void getMyEvaluations_returnsList() {
        when(evaluationRepository.findByJuryId(10L)).thenReturn(List.of());

        assertTrue(evaluationService.getMyEvaluations(10L).isEmpty());
    }

    @Test
    void getMyAssignedSubmissionIds_returnsList() {
        when(juryAssignmentRepository.findSubmissionIdsByJuryId(10L))
                .thenReturn(List.of(1L, 2L));

        List<Long> result = evaluationService.getMyAssignedSubmissionIds(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
    }

    @Test
    void getAverageScoresByRound_groupsCorrectly() {
        Evaluation e1 = new Evaluation();
        e1.setSubmissionId(1L);
        e1.setBackendQuality(10);
        e1.setDatabaseScore(10);

        Evaluation e2 = new Evaluation();
        e2.setSubmissionId(1L);
        e2.setBackendQuality(6);
        e2.setDatabaseScore(6);

        Evaluation e3 = new Evaluation();
        e3.setSubmissionId(2L);
        e3.setBackendQuality(10);
        e3.setDatabaseScore(10);

        when(evaluationRepository.findByRoundId(1L)).thenReturn(List.of(e1, e2, e3));

        Map<Long, Double> averages = evaluationService.getAverageScoresByRound(1L);

        assertEquals(2, averages.size());
        assertTrue(averages.containsKey(1L));
        assertTrue(averages.containsKey(2L));
        assertEquals(8.0, averages.get(1L), 0.01);
        assertEquals(10.0, averages.get(2L), 0.01);
    }

    @Test
    void getAverageScoresByRound_empty_returnsEmptyMap() {
        when(evaluationRepository.findByRoundId(1L)).thenReturn(List.of());

        Map<Long, Double> result = evaluationService.getAverageScoresByRound(1L);

        assertTrue(result.isEmpty());
    }
}

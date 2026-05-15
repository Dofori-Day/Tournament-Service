package org.example.service;

import org.example.dto.SubmissionRequest;
import org.example.model.Round;
import org.example.model.Submission;
import org.example.repository.RoundRepository;
import org.example.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private RoundRepository roundRepository;

    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService(submissionRepository, roundRepository);
    }

    @Test
    void createSubmission_success() {
        SubmissionRequest request = new SubmissionRequest();
        request.setRoundId(1L);
        request.setTeamId(10L);
        request.setGithubLink("https://github.com/test");
        request.setDescription("Test submission");

        Round round = new Round();
        round.setId(1L);
        round.setStatus("ACTIVE");

        when(roundRepository.findById(1L)).thenReturn(round);
        when(submissionRepository.existsByRoundAndTeam(1L, 10L)).thenReturn(false);
        when(submissionRepository.save(any(Submission.class))).thenReturn(100L);

        Long id = submissionService.createSubmission(request);

        assertEquals(100L, id);
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void createSubmission_roundNotFound_throws() {
        SubmissionRequest request = new SubmissionRequest();
        request.setRoundId(999L);

        when(roundRepository.findById(999L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.createSubmission(request));
        assertEquals("Round not found", ex.getMessage());
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void createSubmission_roundNotActive_throws() {
        SubmissionRequest request = new SubmissionRequest();
        request.setRoundId(1L);
        request.setTeamId(10L);

        Round round = new Round();
        round.setId(1L);
        round.setStatus("DRAFT");

        when(roundRepository.findById(1L)).thenReturn(round);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.createSubmission(request));
        assertEquals("Submissions are not open for this round", ex.getMessage());
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void createSubmission_alreadyExists_throws() {
        SubmissionRequest request = new SubmissionRequest();
        request.setRoundId(1L);
        request.setTeamId(10L);

        Round round = new Round();
        round.setId(1L);
        round.setStatus("ACTIVE");

        when(roundRepository.findById(1L)).thenReturn(round);
        when(submissionRepository.existsByRoundAndTeam(1L, 10L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> submissionService.createSubmission(request));
        assertEquals("Your team already submitted for this round", ex.getMessage());
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void updateSubmission_success() {
        SubmissionRequest request = new SubmissionRequest();
        request.setGithubLink("https://github.com/new");
        request.setDescription("Updated");

        Submission existing = new Submission();
        existing.setId(1L);
        existing.setRoundId(1L);

        Round round = new Round();
        round.setId(1L);
        round.setStatus("ACTIVE");

        when(submissionRepository.findById(1L)).thenReturn(existing);
        when(roundRepository.findById(1L)).thenReturn(round);

        submissionService.updateSubmission(1L, request);

        assertEquals("https://github.com/new", existing.getGithubLink());
        assertEquals("Updated", existing.getDescription());
        verify(submissionRepository).update(existing);
    }

    @Test
    void updateSubmission_notFound_throws() {
        when(submissionRepository.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> submissionService.updateSubmission(999L, new SubmissionRequest()));
        verify(submissionRepository, never()).update(any());
    }

    @Test
    void updateSubmission_roundClosed_throws() {
        SubmissionRequest request = new SubmissionRequest();
        Submission existing = new Submission();
        existing.setId(1L);
        existing.setRoundId(1L);

        Round round = new Round();
        round.setId(1L);
        round.setStatus("SUBMISSION_CLOSED");

        when(submissionRepository.findById(1L)).thenReturn(existing);
        when(roundRepository.findById(1L)).thenReturn(round);

        assertThrows(RuntimeException.class,
                () -> submissionService.updateSubmission(1L, request));
        verify(submissionRepository, never()).update(any());
    }

    @Test
    void getSubmission_returnsSubmission() {
        Submission submission = new Submission();
        submission.setId(1L);
        when(submissionRepository.findById(1L)).thenReturn(submission);

        Submission result = submissionService.getSubmission(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSubmission_notFound_returnsNull() {
        when(submissionRepository.findById(999L)).thenReturn(null);

        assertNull(submissionService.getSubmission(999L));
    }

    @Test
    void getSubmissionsByRound_delegates() {
        when(submissionRepository.findByRoundId(1L)).thenReturn(List.of(new Submission()));

        List<Submission> result = submissionService.getSubmissionsByRound(1L);

        assertEquals(1, result.size());
        verify(submissionRepository).findByRoundId(1L);
    }

    @Test
    void getSubmissionsByTeam_delegates() {
        when(submissionRepository.findByTeamId(10L)).thenReturn(List.of());

        List<Submission> result = submissionService.getSubmissionsByTeam(10L);

        assertTrue(result.isEmpty());
        verify(submissionRepository).findByTeamId(10L);
    }
}

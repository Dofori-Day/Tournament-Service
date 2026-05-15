package org.example.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AllDtosTest {

    // --- UserProfileDTO ---

    @Test
    void userProfileDTO_shouldHandleNullValues() {
        UserProfileDTO dto = new UserProfileDTO();
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getRole());
    }

    @Test
    void userProfileDTO_shouldSetAndGet() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName("John");
        dto.setEmail("john@test.com");
        dto.setRole("ADMIN");
        assertEquals("John", dto.getName());
        assertEquals("john@test.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
    }

    // --- AdminProfileDTO ---

    @Test
    void adminProfileDTO_shouldHandleNullTournaments() {
        AdminProfileDTO dto = new AdminProfileDTO();
        assertNull(dto.getCreatedTournaments());
        dto.setCreatedTournaments(new ArrayList<>());
        assertTrue(dto.getCreatedTournaments().isEmpty());
    }

    @Test
    void adminProfileDTO_shouldInheritUserProperties() {
        AdminProfileDTO dto = new AdminProfileDTO();
        dto.setName("Admin");
        dto.setRole("ADMIN");
        assertEquals("Admin", dto.getName());
        assertEquals("ADMIN", dto.getRole());
    }

    // --- JuryProfileDTO ---

    @Test
    void juryProfileDTO_shouldHandleNullEvaluations() {
        JuryProfileDTO dto = new JuryProfileDTO();
        assertNull(dto.getEvaluatedSubmissions());
        dto.setEvaluatedSubmissions(new ArrayList<>());
        assertTrue(dto.getEvaluatedSubmissions().isEmpty());
    }

    // --- TeamProfileDTO ---

    @Test
    void teamProfileDTO_shouldSetAndGet() {
        TeamProfileDTO dto = new TeamProfileDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team A");
        dto.setMembers(new ArrayList<>());
        dto.setSubmissions(new ArrayList<>());
        assertEquals(1L, dto.getTeamId());
        assertEquals("Team A", dto.getTeamName());
        assertTrue(dto.getMembers().isEmpty());
        assertTrue(dto.getSubmissions().isEmpty());
    }

    @Test
    void teamProfileDTO_shouldHandleNullCollections() {
        TeamProfileDTO dto = new TeamProfileDTO();
        assertNull(dto.getMembers());
        assertNull(dto.getSubmissions());
    }

    // --- TeamMemberDTO ---

    @Test
    void teamMemberDTO_shouldHandleBooleans() {
        TeamMemberDTO dto = new TeamMemberDTO();
        assertFalse(dto.isCaptain());
        dto.setCaptain(true);
        assertTrue(dto.isCaptain());
    }

    @Test
    void teamMemberDTO_shouldSetAndGet() {
        TeamMemberDTO dto = new TeamMemberDTO();
        dto.setUserId(10L);
        dto.setName("Member");
        dto.setCaptain(false);
        assertEquals(10L, dto.getUserId());
        assertEquals("Member", dto.getName());
        assertFalse(dto.isCaptain());
    }

    // --- SubmissionHistoryDTO ---

    @Test
    void submissionHistoryDTO_shouldSetAndGet() {
        LocalDateTime now = LocalDateTime.now();
        SubmissionHistoryDTO dto = new SubmissionHistoryDTO();
        dto.setSubmissionId(1L);
        dto.setTournamentId(2L);
        dto.setTournamentTitle("Tourney");
        dto.setStatus("SUBMITTED");
        dto.setSubmittedAt(now);
        assertEquals(1L, dto.getSubmissionId());
        assertEquals(2L, dto.getTournamentId());
        assertEquals("Tourney", dto.getTournamentTitle());
        assertEquals("SUBMITTED", dto.getStatus());
        assertEquals(now, dto.getSubmittedAt());
    }

    @Test
    void submissionHistoryDTO_shouldHandleNullSubmittedAt() {
        SubmissionHistoryDTO dto = new SubmissionHistoryDTO();
        assertNull(dto.getSubmittedAt());
    }

    // --- EvaluatedSubmissionDTO ---

    @Test
    void evaluatedSubmissionDTO_shouldSetAndGet() {
        EvaluatedSubmissionDTO dto = new EvaluatedSubmissionDTO();
        dto.setSubmissionId(1L);
        dto.setTeamId(2L);
        dto.setTeamName("Team");
        dto.setTournamentTitle("Tourney");
        dto.setStatus("EVALUATED");
        dto.setScore(85.5);
        assertEquals(1L, dto.getSubmissionId());
        assertEquals(2L, dto.getTeamId());
        assertEquals("Team", dto.getTeamName());
        assertEquals("Tourney", dto.getTournamentTitle());
        assertEquals("EVALUATED", dto.getStatus());
        assertEquals(85.5, dto.getScore());
    }

    @Test
    void evaluatedSubmissionDTO_shouldHandleNullScore() {
        EvaluatedSubmissionDTO dto = new EvaluatedSubmissionDTO();
        assertNull(dto.getScore());
    }

    // --- TournamentCardDTO ---

    @Test
    void tournamentCardDTO_shouldSetAndGet() {
        TournamentCardDTO dto = new TournamentCardDTO();
        dto.setId(1L);
        dto.setTitle("Title");
        dto.setDescription("Desc");
        dto.setStatus("ACTIVE");
        dto.setFormat("ONLINE");
        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Desc", dto.getDescription());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals("ONLINE", dto.getFormat());
    }

    @Test
    void tournamentCardDTO_shouldHandleNullFields() {
        TournamentCardDTO dto = new TournamentCardDTO();
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
        assertNull(dto.getStatus());
        assertNull(dto.getFormat());
    }

    // --- UserDashboardDTO ---

    @Test
    void userDashboardDTO_shouldSetAndGet() {
        UserDashboardDTO dto = new UserDashboardDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team");
        dto.setTournamentId(2L);
        dto.setTournamentTitle("Tourney");
        dto.setTournamentStatus("ACTIVE");
        dto.setSubmissionId(3L);
        dto.setSubmissionStatus("SUBMITTED");
        assertEquals(1L, dto.getTeamId());
        assertEquals("Team", dto.getTeamName());
        assertEquals(2L, dto.getTournamentId());
        assertEquals("Tourney", dto.getTournamentTitle());
        assertEquals("ACTIVE", dto.getTournamentStatus());
        assertEquals(3L, dto.getSubmissionId());
        assertEquals("SUBMITTED", dto.getSubmissionStatus());
    }

    // --- LeaderboardRowDTO ---

    @Test
    void leaderboardRowDTO_shouldSetAndGet() {
        LeaderboardRowDTO dto = new LeaderboardRowDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team");
        dto.setBackendAvg(8.0);
        dto.setDatabaseAvg(7.5);
        dto.setFrontendAvg(9.0);
        dto.setFunctionalityAvg(8.5);
        dto.setUsabilityAvg(7.0);
        dto.setCompletenessAvg(8.0);
        dto.setTotalScore(8.0);
        assertEquals(1L, dto.getTeamId());
        assertEquals("Team", dto.getTeamName());
        assertEquals(8.0, dto.getBackendAvg());
        assertEquals(7.5, dto.getDatabaseAvg());
        assertEquals(9.0, dto.getFrontendAvg());
        assertEquals(8.5, dto.getFunctionalityAvg());
        assertEquals(7.0, dto.getUsabilityAvg());
        assertEquals(8.0, dto.getCompletenessAvg());
        assertEquals(8.0, dto.getTotalScore());
    }

    @Test
    void leaderboardRowDTO_shouldHandleNullAverages() {
        LeaderboardRowDTO dto = new LeaderboardRowDTO();
        assertNull(dto.getBackendAvg());
        assertNull(dto.getDatabaseAvg());
        assertNull(dto.getFrontendAvg());
        assertNull(dto.getFunctionalityAvg());
        assertNull(dto.getUsabilityAvg());
        assertNull(dto.getCompletenessAvg());
        assertNull(dto.getTotalScore());
    }

    // --- EvaluationExportDto (record) ---

    @Test
    void evaluationExportDto_shouldCreateViaConstructor() {
        LocalDateTime now = LocalDateTime.now();
        EvaluationExportDto dto = new EvaluationExportDto(
            1L, "Jury", 8, 7, 9, 8, 7, 8, 7.83, "Good", now
        );
        assertEquals(1L, dto.submissionId());
        assertEquals("Jury", dto.juryName());
        assertEquals(8, dto.backendQuality());
        assertEquals(7, dto.databaseScore());
        assertEquals(9, dto.frontendQuality());
        assertEquals(8, dto.functionalityScore());
        assertEquals(7, dto.usabilityScore());
        assertEquals(8, dto.mustHaveCompleteness());
        assertEquals(7.83, dto.totalScore());
        assertEquals("Good", dto.comment());
        assertEquals(now, dto.evaluatedAt());
    }

    @Test
    void evaluationExportDto_shouldHandleNullFields() {
        EvaluationExportDto dto = new EvaluationExportDto(
            null, null, null, null, null, null, null, null, null, null, null
        );
        assertNull(dto.submissionId());
        assertNull(dto.juryName());
        assertNull(dto.totalScore());
        assertNull(dto.comment());
    }

    // --- TournamentStatus enum (not a DTO but related) ---
    // TournamentStatus is likely an enum in the model package

    // --- Edge cases ---

    @Test
    void teamMemberDTO_shouldHandleCaptainDefaultIsFalse() {
        TeamMemberDTO dto = new TeamMemberDTO();
        assertFalse(dto.isCaptain());
    }

    @Test
    void userProfileDTO_shouldHandleEmptyStrings() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName("");
        dto.setEmail("");
        dto.setRole("");
        assertEquals("", dto.getName());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getRole());
    }

    @Test
    @SuppressWarnings("unchecked")
    void adminProfileDTO_shouldHandleCreatedTournaments() {
        AdminProfileDTO dto = new AdminProfileDTO();
        TournamentCardDTO card = new TournamentCardDTO();
        card.setId(42L);
        card.setTitle("Test Tournament");
        dto.setCreatedTournaments(List.of(card));
        assertEquals(1, dto.getCreatedTournaments().size());
        assertEquals(42L, dto.getCreatedTournaments().get(0).getId());
        assertEquals("Test Tournament", dto.getCreatedTournaments().get(0).getTitle());
    }

    @Test
    void juryProfileDTO_shouldHandleEvaluatedSubmissions() {
        JuryProfileDTO dto = new JuryProfileDTO();
        EvaluatedSubmissionDTO sub = new EvaluatedSubmissionDTO();
        sub.setSubmissionId(99L);
        sub.setScore(90.0);
        dto.setEvaluatedSubmissions(List.of(sub));
        assertEquals(1, dto.getEvaluatedSubmissions().size());
        assertEquals(99L, dto.getEvaluatedSubmissions().get(0).getSubmissionId());
        assertEquals(90.0, dto.getEvaluatedSubmissions().get(0).getScore());
    }
}

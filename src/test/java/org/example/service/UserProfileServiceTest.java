package org.example.service;

import org.example.dto.*;
import org.example.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository repository;

    private UserProfileService service;

    @BeforeEach
    void setUp() {
        service = new UserProfileService(repository);
    }

    @Test
    void getProfile_withTeamMemberRole_returnsTeamProfileDTO() {
        UserProfileDTO base = new UserProfileDTO();
        base.setName("Team Member");
        base.setEmail("team@test.com");
        base.setRole("TEAM_MEMBER");

        TeamProfileDTO teamProfile = new TeamProfileDTO();
        teamProfile.setTeamId(1L);
        teamProfile.setTeamName("Team Alpha");

        when(repository.getUserBase(1L)).thenReturn(base);
        when(repository.getTeamProfile(1L)).thenReturn(teamProfile);
        when(repository.getTeamMembers(1L)).thenReturn(List.of());
        when(repository.getTeamSubmissions(1L)).thenReturn(List.of());

        Object result = service.getProfile(1L);

        assertInstanceOf(TeamProfileDTO.class, result);
        TeamProfileDTO team = (TeamProfileDTO) result;
        assertEquals("Team Alpha", team.getTeamName());
        assertEquals(1L, team.getTeamId());
        verify(repository).getTeamMembers(1L);
        verify(repository).getTeamSubmissions(1L);
    }

    @Test
    void getProfile_withJuryRole_returnsJuryProfileDTO() {
        UserProfileDTO base = new UserProfileDTO();
        base.setName("Juror");
        base.setEmail("jury@test.com");
        base.setRole("JURY");

        when(repository.getUserBase(2L)).thenReturn(base);
        when(repository.getJuryEvaluations(2L)).thenReturn(List.of());

        Object result = service.getProfile(2L);

        assertInstanceOf(JuryProfileDTO.class, result);
        JuryProfileDTO jury = (JuryProfileDTO) result;
        assertEquals("Juror", jury.getName());
        assertEquals("jury@test.com", jury.getEmail());
        assertEquals("JURY", jury.getRole());
    }

    @Test
    void getProfile_withAdminRole_returnsAdminProfileDTO() {
        UserProfileDTO base = new UserProfileDTO();
        base.setName("Admin");
        base.setEmail("admin@test.com");
        base.setRole("ADMIN");

        when(repository.getUserBase(3L)).thenReturn(base);
        when(repository.getCreatedTournaments(3L)).thenReturn(List.of());

        Object result = service.getProfile(3L);

        assertInstanceOf(AdminProfileDTO.class, result);
        AdminProfileDTO admin = (AdminProfileDTO) result;
        assertEquals("Admin", admin.getName());
        assertEquals("ADMIN", admin.getRole());
        verify(repository).getCreatedTournaments(3L);
    }

    @Test
    void getProfile_withUnknownRole_throwsRuntimeException() {
        UserProfileDTO base = new UserProfileDTO();
        base.setRole("UNKNOWN_ROLE");
        when(repository.getUserBase(4L)).thenReturn(base);

        assertThrows(RuntimeException.class, () -> service.getProfile(4L));
    }

    @Test
    void getProfile_teamMemberWithMembersAndSubmissions() {
        UserProfileDTO base = new UserProfileDTO();
        base.setRole("TEAM_MEMBER");

        TeamProfileDTO teamProfile = new TeamProfileDTO();
        teamProfile.setTeamId(5L);
        teamProfile.setTeamName("Team Beta");

        TeamMemberDTO member = new TeamMemberDTO();
        member.setUserId(1L);
        member.setName("Member 1");
        member.setCaptain(true);

        SubmissionHistoryDTO sub = new SubmissionHistoryDTO();
        sub.setSubmissionId(10L);
        sub.setStatus("SUBMITTED");

        when(repository.getUserBase(5L)).thenReturn(base);
        when(repository.getTeamProfile(5L)).thenReturn(teamProfile);
        when(repository.getTeamMembers(5L)).thenReturn(List.of(member));
        when(repository.getTeamSubmissions(5L)).thenReturn(List.of(sub));

        TeamProfileDTO result = (TeamProfileDTO) service.getProfile(5L);

        assertEquals(1, result.getMembers().size());
        assertEquals("Member 1", result.getMembers().get(0).getName());
        assertTrue(result.getMembers().get(0).isCaptain());
        assertEquals(1, result.getSubmissions().size());
        assertEquals(10L, result.getSubmissions().get(0).getSubmissionId());
    }
}

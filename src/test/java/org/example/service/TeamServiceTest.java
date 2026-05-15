package org.example.service;

import org.example.dto.CreateTeamRequest;
import org.example.repository.TeamMemberRepository;
import org.example.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(teamRepository, teamMemberRepository);
    }

    @Test
    void createTeam_returnsTeamId() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Team Awesome");
        request.setTournamentId(1L);
        request.setCaptainId(10L);
        request.setOrganization("Org");
        request.setContactTelegram("@telegram");
        request.setContactDiscord("@discord");

        when(teamRepository.createTeam("Team Awesome", 1L, 10L, "Org", "@telegram", "@discord", "PENDING"))
                .thenReturn(42L);

        Long teamId = teamService.createTeam(request);

        assertEquals(42L, teamId);
        verify(teamMemberRepository).addMember(42L, 10L, true);
    }
}

package org.example.web;

import org.example.dto.TeamProfileDTO;
import org.example.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileService service;

    @Test
    void getProfile_returnsProfile() throws Exception {
        TeamProfileDTO dto = new TeamProfileDTO();
        dto.setTeamId(1L);
        dto.setTeamName("Team A");
        when(service.getProfile(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value(1))
                .andExpect(jsonPath("$.teamName").value("Team A"));
    }
}

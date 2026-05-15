package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void register_success() throws Exception {
        User user = new User(null, "newuser", "pass", "New User");
        when(userService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully with login: newuser"));
    }

    @Test
    void register_failure() throws Exception {
        User user = new User(null, "exists", "pass", "Existing");
        when(userService.register(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed (maybe login exists)"));
    }

    @Test
    void login_success() throws Exception {
        User user = new User(1L, "testuser", "pass", "Test User");
        Role role = new Role(1L, "TEAM_MEMBER");
        user.setRoles(Set.of(role));

        when(userService.authenticateUser("testuser", "pass")).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void login_invalidCredentials() throws Exception {
        User user = new User(null, "testuser", "wrongpass", "Test");
        when(userService.authenticateUser("testuser", "wrongpass")).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }
}

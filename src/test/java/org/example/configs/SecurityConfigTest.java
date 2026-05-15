package org.example.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
    classes = TestSecurityApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void securityConfig_shouldBeLoaded() {
        assertNotNull(securityConfig);
    }

    @Test
    void passwordEncoder_shouldBeCreated() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void authEndpoint_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk());
    }

    @Test
    void apiEndpoint_shouldRequireAuth() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void optionsRequest_shouldBePermitted() throws Exception {
        mockMvc.perform(options("/api/test"))
                .andExpect(status().isOk());
    }
}

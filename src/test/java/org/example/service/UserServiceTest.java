package org.example.service;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void register_success() {
        User user = new User(null, "testuser", "rawpass", "Test User");
        when(userRepository.save(any(User.class))).thenReturn(1);

        User result = userService.register(user);

        assertNotNull(result);
        assertEquals("testuser", result.getLogin());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_setsDefaultRoleWhenRolesNull() {
        User user = new User(null, "newuser", "pass", "New User");
        user.setRoles(null);
        when(userRepository.save(any(User.class))).thenReturn(1);

        User result = userService.register(user);

        assertNotNull(result);
        assertNotNull(result.getRoles());
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().stream().anyMatch(r -> r.getId() == 1L));
    }

    @Test
    void register_setsDefaultRoleWhenRolesEmpty() {
        User user = new User(null, "newuser", "pass", "New User");
        user.setRoles(Set.of());
        when(userRepository.save(any(User.class))).thenReturn(1);

        User result = userService.register(user);

        assertNotNull(result);
        assertEquals(1, result.getRoles().size());
    }

    @Test
    void register_encodesPassword() {
        User user = new User(null, "encuser", "plaintext", "Enc User");
        when(userRepository.save(any(User.class))).thenReturn(1);

        User result = userService.register(user);

        assertNotNull(result);
        assertNotEquals("plaintext", result.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("plaintext", result.getPassword()));
    }

    @Test
    void register_saveFails_returnsNull() {
        User user = new User(null, "failuser", "pass", "Fail User");
        when(userRepository.save(any(User.class))).thenReturn(0);

        User result = userService.register(user);

        assertNull(result);
    }

    @Test
    void authenticate_success() {
        User user = new User(1L, "authuser", new BCryptPasswordEncoder().encode("correctpass"), "Auth User");
        when(userRepository.findByLogin("authuser")).thenReturn(user);

        boolean result = userService.authenticate("authuser", "correctpass");

        assertTrue(result);
    }

    @Test
    void authenticate_wrongPassword_returnsFalse() {
        User user = new User(1L, "authuser", new BCryptPasswordEncoder().encode("correctpass"), "Auth User");
        when(userRepository.findByLogin("authuser")).thenReturn(user);

        boolean result = userService.authenticate("authuser", "wrongpass");

        assertFalse(result);
    }

    @Test
    void authenticate_userNotFound_returnsFalse() {
        when(userRepository.findByLogin("unknown")).thenReturn(null);

        boolean result = userService.authenticate("unknown", "anypass");

        assertFalse(result);
    }

    @Test
    void authenticateUser_success() {
        User user = new User(1L, "authuser", new BCryptPasswordEncoder().encode("pass"), "Auth User");
        Role role = new Role(1L, "TEAM_MEMBER");
        user.setRoles(Set.of(role));
        when(userRepository.findByLogin("authuser")).thenReturn(user);

        User result = userService.authenticateUser("authuser", "pass");

        assertNotNull(result);
        assertEquals("authuser", result.getLogin());
    }

    @Test
    void authenticateUser_wrongPassword_returnsNull() {
        User user = new User(1L, "authuser", new BCryptPasswordEncoder().encode("pass"), "Auth User");
        when(userRepository.findByLogin("authuser")).thenReturn(user);

        User result = userService.authenticateUser("authuser", "wrong");

        assertNull(result);
    }

    @Test
    void authenticateUser_userNotFound_returnsNull() {
        when(userRepository.findByLogin("unknown")).thenReturn(null);

        User result = userService.authenticateUser("unknown", "pass");

        assertNull(result);
    }
}

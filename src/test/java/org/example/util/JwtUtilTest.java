package org.example.util;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken("testuser", "Test User", Set.of("TEAM_MEMBER"));
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_returnsCorrectSubject() {
        String token = jwtUtil.generateToken("john", "John Doe", Set.of("ADMIN"));
        assertEquals("john", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_withCorrectUsername_returnsTrue() {
        String token = jwtUtil.generateToken("alice", "Alice", Set.of("JURY"));
        assertTrue(jwtUtil.isTokenValid(token, "alice"));
    }

    @Test
    void isTokenValid_withWrongUsername_returnsFalse() {
        String token = jwtUtil.generateToken("alice", "Alice", Set.of("JURY"));
        assertFalse(jwtUtil.isTokenValid(token, "bob"));
    }

    @Test
    void extractAllClaims_containsExpectedClaims() {
        String token = jwtUtil.generateToken("bob", "Bob", Set.of("TEAM_MEMBER", "JURY"));
        var claims = jwtUtil.extractAllClaims(token);
        assertEquals("bob", claims.getSubject());
        assertNotNull(claims.get("roles"));
        assertNotNull(claims.get("name"));
        assertEquals("Bob", claims.get("name"));
    }

    @Test
    void generateToken_withMultipleRoles() {
        String token = jwtUtil.generateToken("multi", "Multi Role", Set.of("TEAM_MEMBER", "JURY", "ADMIN"));
        var claims = jwtUtil.extractAllClaims(token);
        assertTrue(claims.get("roles") instanceof java.util.List);
        assertEquals(3, ((java.util.List<?>) claims.get("roles")).size());
    }

    @Test
    void generateToken_setsIssuedAndExpiration() {
        String token = jwtUtil.generateToken("test", "Test", Set.of("TEAM_MEMBER"));
        var claims = jwtUtil.extractAllClaims(token);
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}

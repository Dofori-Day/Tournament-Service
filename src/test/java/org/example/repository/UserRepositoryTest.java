package org.example.repository;

import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
})
class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepository(jdbcTemplate);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "login VARCHAR(100), " +
            "password VARCHAR(100), " +
            "name VARCHAR(100)" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS roles (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "role_name VARCHAR(50)" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_roles (" +
            "user_id BIGINT, " +
            "role_id BIGINT" +
            ")");
    }

    @Test
    void findByLogin_whenExists_returnsUser() {
        jdbcTemplate.update("INSERT INTO users (id, login, password, name) VALUES (1, 'testuser', 'pass123', 'Test User')");

        User user = repository.findByLogin("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getLogin());
        assertEquals("Test User", user.getName());
    }

    @Test
    void findByLogin_whenNotExists_returnsNull() {
        User user = repository.findByLogin("nonexistent");
        assertNull(user);
    }

    @Test
    void findById_whenExists_returnsUser() {
        jdbcTemplate.update("INSERT INTO users (id, login, password, name) VALUES (2, 'user2', 'pass', 'User Two')");

        User user = repository.findById(2L);
        assertNotNull(user);
        assertEquals("user2", user.getLogin());
    }

    @Test
    void findById_whenNotExists_returnsNull() {
        User user = repository.findById(999L);
        assertNull(user);
    }
}

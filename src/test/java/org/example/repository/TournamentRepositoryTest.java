package org.example.repository;

import org.example.dto.TournamentCardDTO;
import org.example.model.Tournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE"
})
class TournamentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private TournamentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TournamentRepository(jdbcTemplate);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS roles (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(50), " +
            "role_name VARCHAR(50)" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_roles (" +
            "user_id BIGINT, " +
            "role_id BIGINT" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "login VARCHAR(100), " +
            "password VARCHAR(100), " +
            "name VARCHAR(100)" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tournaments (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "title VARCHAR(200), " +
            "description VARCHAR(1000), " +
            "rules VARCHAR(1000), " +
            "start_date TIMESTAMP, " +
            "registration_start TIMESTAMP, " +
            "registration_end TIMESTAMP, " +
            "max_teams INT, " +
            "format VARCHAR(50), " +
            "status VARCHAR(50), " +
            "created_by BIGINT, " +
            "created_at TIMESTAMP, " +
            "updated_at TIMESTAMP" +
            ")");
    }

    @Test
    void isOrganizer_whenUserHasRole_returnsTrue() {
        jdbcTemplate.update("INSERT INTO roles (id, name, role_name) VALUES (1, 'ORGANIZER', 'ORGANIZER')");
        jdbcTemplate.update("INSERT INTO users (id, login, password, name) VALUES (1, 'org', 'pass', 'Organizer')");
        jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (1, 1)");

        assertTrue(repository.isOrganizer(1L));
    }

    @Test
    void isOrganizer_whenUserDoesNotHaveRole_returnsFalse() {
        jdbcTemplate.update("INSERT INTO users (id, login, password, name) VALUES (2, 'user', 'pass', 'User')");

        assertFalse(repository.isOrganizer(2L));
    }

    @Test
    void createTournament_shouldInsert() {
        Tournament t = new Tournament();
        t.setTitle("Test Tournament");
        t.setDescription("Description");
        t.setStartDate(LocalDateTime.now().plusDays(1));
        t.setRegistrationStart(LocalDateTime.now());
        t.setRegistrationEnd(LocalDateTime.now().plusDays(7));
        t.setMaxTeams(20);
        t.setFormat("ONLINE");
        t.setStatus("DRAFT");
        t.setCreatedBy(1L);

        repository.createTournament(t);

        List<TournamentCardDTO> all = repository.getTournaments(null);
        assertFalse(all.isEmpty());
        assertEquals("Test Tournament", all.get(0).getTitle());
    }

    @Test
    void getTournaments_withStatus_returnsFiltered() {
        Tournament t1 = new Tournament();
        t1.setTitle("Active");
        t1.setStartDate(LocalDateTime.now().plusDays(1));
        t1.setRegistrationStart(LocalDateTime.now());
        t1.setRegistrationEnd(LocalDateTime.now().plusDays(7));
        t1.setMaxTeams(10);
        t1.setFormat("ONLINE");
        t1.setStatus("ACTIVE");
        t1.setCreatedBy(1L);
        repository.createTournament(t1);

        Tournament t2 = new Tournament();
        t2.setTitle("Draft");
        t2.setStartDate(LocalDateTime.now().plusDays(1));
        t2.setRegistrationStart(LocalDateTime.now());
        t2.setRegistrationEnd(LocalDateTime.now().plusDays(7));
        t2.setMaxTeams(10);
        t2.setFormat("ONLINE");
        t2.setStatus("DRAFT");
        t2.setCreatedBy(1L);
        repository.createTournament(t2);

        List<TournamentCardDTO> active = repository.getTournaments("ACTIVE");
        assertEquals(1, active.size());
        assertEquals("Active", active.get(0).getTitle());
    }
}

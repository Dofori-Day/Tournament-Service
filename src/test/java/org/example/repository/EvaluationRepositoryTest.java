package org.example.repository;

import org.example.model.Evaluation;
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
class EvaluationRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EvaluationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EvaluationRepository(jdbcTemplate);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "login VARCHAR(100), " +
            "password VARCHAR(100), " +
            "name VARCHAR(100), " +
            "email VARCHAR(100), " +
            "role VARCHAR(50)" +
            ")");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS evaluations (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "submission_id BIGINT, " +
            "jury_id BIGINT, " +
            "backend_quality INT, " +
            "database_score INT, " +
            "frontend_quality INT, " +
            "functionality_score INT, " +
            "usability_score INT, " +
            "must_have_completeness INT, " +
            "comment VARCHAR(500), " +
            "evaluated_at TIMESTAMP, " +
            "CONSTRAINT uk_sub_jury UNIQUE(submission_id, jury_id)" +
            ")");

        jdbcTemplate.update("INSERT INTO users (id, login, password, name, role) VALUES (1, 'jury1', 'pass', 'Jury One', 'JURY')");
    }

    @Test
    void findBySubmissionAndJury_whenFound_returnsEvaluation() {
        jdbcTemplate.update(
            "INSERT INTO evaluations (submission_id, jury_id, backend_quality, database_score, frontend_quality, " +
            "functionality_score, usability_score, must_have_completeness, comment, evaluated_at) " +
            "VALUES (100, 1, 8, 7, 9, 8, 7, 8, 'Good work', NOW())"
        );

        Evaluation found = repository.findBySubmissionAndJury(100L, 1L);
        assertNotNull(found);
        assertEquals(100L, found.getSubmissionId());
        assertEquals(1L, found.getJuryId());
        assertEquals(8, found.getBackendQuality());
        assertEquals("Good work", found.getComment());
    }

    @Test
    void findBySubmissionAndJury_whenNotFound_returnsNull() {
        Evaluation found = repository.findBySubmissionAndJury(999L, 999L);
        assertNull(found);
    }

    @Test
    void findBySubmissionId_shouldReturnEvaluations() {
        jdbcTemplate.update(
            "INSERT INTO evaluations (submission_id, jury_id, backend_quality, database_score, frontend_quality, " +
            "functionality_score, usability_score, must_have_completeness, comment, evaluated_at) " +
            "VALUES (200, 1, 8, 8, 8, 8, 8, 8, 'Nice', NOW())"
        );

        var list = repository.findBySubmissionId(200L);
        assertEquals(1, list.size());
        assertEquals(200L, list.get(0).getSubmissionId());
        assertEquals(8, list.get(0).getFunctionalityScore());
    }

    @Test
    void findByJuryId_shouldReturnEvaluations() {
        jdbcTemplate.update(
            "INSERT INTO evaluations (submission_id, jury_id, backend_quality, database_score, frontend_quality, " +
            "functionality_score, usability_score, must_have_completeness, comment, evaluated_at) " +
            "VALUES (300, 1, 7, 7, 7, 7, 7, 7, 'OK', NOW())"
        );

        var list = repository.findByJuryId(1L);
        assertEquals(1, list.size());
        assertEquals(300L, list.get(0).getSubmissionId());
    }

    @Test
    void findAll_shouldReturnAllEvaluations() {
        jdbcTemplate.update(
            "INSERT INTO evaluations (submission_id, jury_id, backend_quality, database_score, frontend_quality, " +
            "functionality_score, usability_score, must_have_completeness, comment, evaluated_at) " +
            "VALUES (400, 1, 9, 9, 9, 9, 9, 9, 'Excellent', NOW())"
        );

        var list = repository.findAll();
        assertEquals(1, list.size());
        assertEquals(400L, list.get(0).getSubmissionId());
    }
}

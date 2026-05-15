package org.example.configs;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

class MySqlConfigTest {

    private final MySqlConfig config = new MySqlConfig();

    @Test
    void mysqlDataSource_shouldCreateHikariDataSource() {
        HikariDataSource ds = config.mysqlDataSource();
        assertNotNull(ds);
        assertTrue(ds instanceof HikariDataSource);
        ds.close();
    }

    @Test
    void mysqlJdbcTemplate_shouldWrapDataSource() {
        HikariDataSource ds = config.mysqlDataSource();
        JdbcTemplate template = config.mysqlJdbcTemplate(ds);
        assertNotNull(template);
        assertNotNull(template.getDataSource());
        ds.close();
    }
}

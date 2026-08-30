package com.acme.salary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Verifies the Flyway migration builds the expected schema against a real
 * PostgreSQL — the local docker-compose database (see docker-compose.yml).
 *
 * <p>Runs only when that database is reachable, so the suite stays green even
 * when it is not started; the pure unit tests never depend on it. Start it with
 * {@code docker compose up -d} to include this test.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnabledIf("com.acme.salary.support.LocalPostgres#isReachable")
class SchemaMigrationTest {

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void migrationCreatesCoreTables() {
        List<String> tables = jdbc.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'",
                String.class);

        assertThat(tables).contains("employees", "salaries", "currency_rates");
    }

    @Test
    void employeesTableHasExpectedColumns() {
        List<String> columns = jdbc.queryForList(
                "SELECT column_name FROM information_schema.columns "
                        + "WHERE table_name = 'employees'",
                String.class);

        assertThat(columns).contains(
                "id", "first_name", "last_name", "email", "country", "department",
                "job_title", "join_date");
    }
}

package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Persistence test for employees. Runs against the local docker-compose Postgres
 * (not an embedded database) so the JPA mapping is validated against the real
 * Flyway schema. Transactional by default, so each test rolls back and leaves
 * the database clean.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIf("com.acme.salary.support.LocalPostgres#isReachable")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void savesAndReadsBackAnEmployee() {
        Employee saved = repository.save(new Employee(
                "Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Software Engineer",
                LocalDate.of(2021, 3, 1)));

        assertThat(saved.getId()).isNotNull();

        Optional<Employee> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("ada@acme.com");
        assertThat(found.get().getDepartment()).isEqualTo("Engineering");
        assertThat(found.get().getCountry()).isEqualTo("United Kingdom");
    }
}

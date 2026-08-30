package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.salary.Salary;
import com.acme.salary.salary.SalaryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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

    @Autowired
    private SalaryRepository salaries;

    @Autowired
    private CurrencyRateRepository rates;

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

    @Test
    void findsEmployeesJoinedWithTheirSalary() {
        rates.save(new CurrencyRate("USD", new BigDecimal("1.000000")));
        Employee employee = repository.save(new Employee(
                "Grace", "Hopper", "grace@acme.com",
                "United States", "Engineering", "Principal Engineer",
                LocalDate.of(2019, 6, 1)));
        salaries.save(new Salary(employee.getId(), new BigDecimal("185000.00"), "USD",
                LocalDate.of(2022, 1, 1)));

        Page<EmployeeSalaryRow> page = repository.findAllWithSalary(PageRequest.of(0, 10));

        assertThat(page.getContent()).anySatisfy(row -> {
            assertThat(row.email()).isEqualTo("grace@acme.com");
            assertThat(row.salaryAmount()).isEqualByComparingTo("185000.00");
            assertThat(row.currency()).isEqualTo("USD");
        });
    }
}

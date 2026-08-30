package com.acme.salary.seed;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.employee.EmployeeRepository;
import com.acme.salary.salary.SalaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies the seeder persists a consistent set of employees, salaries, and
 * currency rates. Runs against the test database and rolls back afterwards.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@EnabledIf("com.acme.salary.support.LocalPostgres#isReachable")
class DataSeederTest {

    @Autowired
    private DataSeeder seeder;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private SalaryRepository salaries;

    @Autowired
    private CurrencyRateRepository rates;

    @Test
    void seedsEmployeesEachWithASalaryPlusCurrencyRates() {
        int created = seeder.seed(25, 123L);

        assertThat(created).isEqualTo(25);
        assertThat(employees.count()).isEqualTo(25);
        assertThat(salaries.count()).isEqualTo(25);
        assertThat(rates.count()).isGreaterThanOrEqualTo(6);
    }
}

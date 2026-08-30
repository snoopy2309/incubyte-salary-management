package com.acme.salary.salary;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Persistence test for salaries. A salary references an employee and holds an
 * amount in a local currency (whose rate lives in currency_rates). Verifies the
 * mapping and the foreign-key links against the real Flyway schema.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIf("com.acme.salary.support.LocalPostgres#isReachable")
class SalaryRepositoryTest {

    @Autowired
    private SalaryRepository salaries;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private CurrencyRateRepository rates;

    @Test
    void savesSalaryLinkedToAnEmployeeAndCurrency() {
        rates.save(new CurrencyRate("USD", new BigDecimal("1.000000")));
        Employee employee = employees.save(new Employee(
                "Grace", "Hopper", "grace@acme.com",
                "United States", "Engineering", "Principal Engineer",
                LocalDate.of(2019, 6, 1)));

        Salary saved = salaries.save(new Salary(
                employee.getId(), new BigDecimal("185000.00"), "USD",
                LocalDate.of(2022, 1, 1)));

        assertThat(saved.getId()).isNotNull();

        Salary found = salaries.findById(saved.getId()).orElseThrow();
        assertThat(found.getEmployeeId()).isEqualTo(employee.getId());
        assertThat(found.getAmount()).isEqualByComparingTo("185000.00");
        assertThat(found.getCurrency()).isEqualTo("USD");
        assertThat(found.getEffectiveDate()).isEqualTo(LocalDate.of(2022, 1, 1));
    }
}

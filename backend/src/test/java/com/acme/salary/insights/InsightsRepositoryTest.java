package com.acme.salary.insights;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeRepository;
import com.acme.salary.salary.Salary;
import com.acme.salary.salary.SalaryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Verifies the summary aggregation against real Postgres: salaries in different
 * currencies are normalised to USD before summing/averaging, and the median is
 * computed with percentile_cont. Rolls back afterwards.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@EnabledIf("com.acme.salary.support.LocalPostgres#isReachable")
class InsightsRepositoryTest {

    @Autowired
    private InsightsRepository insights;

    @Autowired
    private EmployeeRepository employees;

    @Autowired
    private SalaryRepository salaries;

    @Autowired
    private CurrencyRateRepository rates;

    @Test
    void summarisesSalariesNormalisedToUsd() {
        rates.save(new CurrencyRate("USD", new BigDecimal("1.000000")));
        rates.save(new CurrencyRate("INR", new BigDecimal("0.012000")));

        Employee usEmployee = employees.save(new Employee("Grace", "Hopper", "grace@acme.com",
                "United States", "Engineering", "Principal Engineer", LocalDate.of(2019, 6, 1)));
        Employee inEmployee = employees.save(new Employee("Aarav", "Patel", "aarav@acme.com",
                "India", "Engineering", "Software Engineer", LocalDate.of(2021, 2, 1)));

        salaries.save(new Salary(usEmployee.getId(), new BigDecimal("100000.00"), "USD",
                LocalDate.of(2022, 1, 1)));
        // 1,000,000 INR * 0.012 = 12,000 USD
        salaries.save(new Salary(inEmployee.getId(), new BigDecimal("1000000.00"), "INR",
                LocalDate.of(2022, 1, 1)));

        SalarySummary summary = insights.fetchSummary();

        assertThat(summary.headcount()).isEqualTo(2);
        assertThat(summary.totalUsd()).isEqualByComparingTo("112000");   // 100000 + 12000
        assertThat(summary.averageUsd()).isEqualByComparingTo("56000");  // mean
        assertThat(summary.medianUsd()).isEqualByComparingTo("56000");   // (12000 + 100000) / 2
    }

    @Test
    void groupsSalariesByCountryInUsd() {
        rates.save(new CurrencyRate("USD", new BigDecimal("1.000000")));
        rates.save(new CurrencyRate("INR", new BigDecimal("0.012000")));

        Employee us1 = employees.save(new Employee("Grace", "Hopper", "grace@acme.com",
                "United States", "Engineering", "Principal Engineer", LocalDate.of(2019, 6, 1)));
        Employee us2 = employees.save(new Employee("Alan", "Turing", "alan@acme.com",
                "United States", "Engineering", "Staff Engineer", LocalDate.of(2020, 1, 1)));
        Employee in1 = employees.save(new Employee("Aarav", "Patel", "aarav@acme.com",
                "India", "Sales", "Manager", LocalDate.of(2021, 2, 1)));

        salaries.save(new Salary(us1.getId(), new BigDecimal("100000.00"), "USD", LocalDate.of(2022, 1, 1)));
        salaries.save(new Salary(us2.getId(), new BigDecimal("200000.00"), "USD", LocalDate.of(2022, 1, 1)));
        salaries.save(new Salary(in1.getId(), new BigDecimal("1000000.00"), "INR", LocalDate.of(2022, 1, 1)));

        List<GroupSummary> byCountry = insights.fetchByCountry();

        GroupSummary us = groupNamed(byCountry, "United States");
        assertThat(us.headcount()).isEqualTo(2);
        assertThat(us.totalUsd()).isEqualByComparingTo("300000");
        assertThat(us.averageUsd()).isEqualByComparingTo("150000");
        assertThat(us.medianUsd()).isEqualByComparingTo("150000");

        GroupSummary india = groupNamed(byCountry, "India");
        assertThat(india.headcount()).isEqualTo(1);
        assertThat(india.totalUsd()).isEqualByComparingTo("12000"); // 1,000,000 INR * 0.012
    }

    private static GroupSummary groupNamed(List<GroupSummary> groups, String name) {
        return groups.stream().filter(g -> g.name().equals(name)).findFirst().orElseThrow();
    }
}

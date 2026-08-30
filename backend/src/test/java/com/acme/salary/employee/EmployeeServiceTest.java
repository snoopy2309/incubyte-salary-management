package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.salary.common.PagedResponse;
import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit test: the service joins employees with salaries and normalises each
 * salary to USD using the currency rates.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CurrencyRateRepository rateRepository;

    @InjectMocks
    private EmployeeService service;

    @Test
    void listsEmployeesWithSalaryConvertedToUsd() {
        EmployeeSalaryRow row = new EmployeeSalaryRow(1L, "Ada", "Lovelace", "ada@acme.com",
                "India", "Engineering", "Software Engineer",
                new BigDecimal("1000000"), "INR");
        when(employeeRepository.findAllWithSalary(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row)));
        when(rateRepository.findAll())
                .thenReturn(List.of(new CurrencyRate("INR", new BigDecimal("0.012"))));

        PagedResponse<EmployeeSummary> result = service.list(PageRequest.of(0, 20));

        assertThat(result.totalElements()).isEqualTo(1);
        EmployeeSummary summary = result.content().get(0);
        assertThat(summary.email()).isEqualTo("ada@acme.com");
        assertThat(summary.salaryAmount()).isEqualByComparingTo("1000000");
        assertThat(summary.currency()).isEqualTo("INR");
        // 1,000,000 INR * 0.012 = 12,000.00 USD
        assertThat(summary.salaryUsd()).isEqualByComparingTo("12000.00");
    }
}

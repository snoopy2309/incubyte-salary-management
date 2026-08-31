package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import com.acme.salary.currency.CurrencyConverter;
import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application logic for browsing employees, with salaries normalised to USD. */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CurrencyRateRepository rateRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           CurrencyRateRepository rateRepository) {
        this.employeeRepository = employeeRepository;
        this.rateRepository = rateRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeSummary> list(String country, String department, String q,
                                               Pageable pageable) {
        CurrencyConverter converter = loadConverter();
        return PagedResponse.from(
                employeeRepository.findAllWithSalary(
                                blankToNull(country), blankToNull(department), blankToNull(q), pageable)
                        .map(row -> toSummary(row, converter)));
    }

    private CurrencyConverter loadConverter() {
        Map<String, BigDecimal> rates = rateRepository.findAll().stream()
                .collect(Collectors.toMap(CurrencyRate::getCurrency, CurrencyRate::getRateToUsd));
        return new CurrencyConverter(rates);
    }

    private static EmployeeSummary toSummary(EmployeeSalaryRow row, CurrencyConverter converter) {
        return new EmployeeSummary(
                row.id(), row.firstName(), row.lastName(), row.email(),
                row.country(), row.department(), row.jobTitle(),
                row.salaryAmount(), row.currency(),
                converter.toUsd(row.salaryAmount(), row.currency()));
    }

    /** Treat blank/whitespace filter values as "no filter". */
    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}

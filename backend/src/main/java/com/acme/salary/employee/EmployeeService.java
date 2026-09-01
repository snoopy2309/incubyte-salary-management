package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import com.acme.salary.currency.CurrencyConverter;
import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.salary.Salary;
import com.acme.salary.salary.SalaryRepository;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application logic for browsing and managing employees, salaries normalised to USD. */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final CurrencyRateRepository rateRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           SalaryRepository salaryRepository,
                           CurrencyRateRepository rateRepository) {
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
        this.rateRepository = rateRepository;
    }

    @Transactional
    public EmployeeSummary updateSalary(Long employeeId, BigDecimal amount, String currency) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("No employee with id " + employeeId));
        requireKnownCurrency(currency);
        Salary salary = salaryRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("No salary for employee " + employeeId));
        salary.changeTo(amount, currency);
        salaryRepository.save(salary);
        return toSummary(employee, amount, currency, loadConverter());
    }

    @Transactional
    public EmployeeSummary createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An employee with email " + request.email() + " already exists");
        }
        requireKnownCurrency(request.currency());
        Employee employee = employeeRepository.save(new Employee(
                request.firstName(), request.lastName(), request.email(), request.country(),
                request.department(), request.jobTitle(), request.joinDate()));
        salaryRepository.save(new Salary(
                employee.getId(), request.salaryAmount(), request.currency(), request.joinDate()));
        return toSummary(employee, request.salaryAmount(), request.currency(), loadConverter());
    }

    private void requireKnownCurrency(String currency) {
        if (!rateRepository.existsById(currency)) {
            throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }

    private static EmployeeSummary toSummary(Employee e, BigDecimal amount, String currency,
                                             CurrencyConverter converter) {
        return new EmployeeSummary(e.getId(), e.getFirstName(), e.getLastName(), e.getEmail(),
                e.getCountry(), e.getDepartment(), e.getJobTitle(),
                amount, currency, converter.toUsd(amount, currency));
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

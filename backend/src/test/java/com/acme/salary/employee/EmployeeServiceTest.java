package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salary.common.PagedResponse;
import com.acme.salary.currency.CurrencyRate;
import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.salary.Salary;
import com.acme.salary.salary.SalaryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private CurrencyRateRepository rateRepository;

    @InjectMocks
    private EmployeeService service;

    @Test
    void listsEmployeesWithSalaryConvertedToUsd() {
        EmployeeSalaryRow row = new EmployeeSalaryRow(1L, "Ada", "Lovelace", "ada@acme.com",
                "India", "Engineering", "Software Engineer",
                new BigDecimal("1000000"), "INR");
        when(employeeRepository.findAllWithSalary(
                nullable(String.class), nullable(String.class), nullable(String.class),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row)));
        when(rateRepository.findAll())
                .thenReturn(List.of(new CurrencyRate("INR", new BigDecimal("0.012"))));

        PagedResponse<EmployeeSummary> result = service.list(null, null, null, PageRequest.of(0, 20));

        assertThat(result.totalElements()).isEqualTo(1);
        EmployeeSummary summary = result.content().get(0);
        assertThat(summary.email()).isEqualTo("ada@acme.com");
        assertThat(summary.salaryUsd()).isEqualByComparingTo("12000.00");
    }

    @Test
    void updatesAnEmployeesSalary() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Engineer", LocalDate.of(2020, 1, 1));
        Salary salary = new Salary(1L, new BigDecimal("50000"), "GBP", LocalDate.of(2020, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(rateRepository.existsById("GBP")).thenReturn(true);
        when(salaryRepository.findByEmployeeId(1L)).thenReturn(Optional.of(salary));
        when(rateRepository.findAll())
                .thenReturn(List.of(new CurrencyRate("GBP", new BigDecimal("1.27"))));

        EmployeeSummary result = service.updateSalary(1L, new BigDecimal("90000"), "GBP");

        assertThat(result.salaryAmount()).isEqualByComparingTo("90000");
        assertThat(result.salaryUsd()).isEqualByComparingTo("114300.00"); // 90000 * 1.27
        assertThat(salary.getAmount()).isEqualByComparingTo("90000");
        verify(salaryRepository).save(salary);
    }

    @Test
    void rejectsAnUnknownCurrencyWhenUpdating() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Engineer", LocalDate.of(2020, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(rateRepository.existsById("XYZ")).thenReturn(false);

        assertThatThrownBy(() -> service.updateSalary(1L, new BigDecimal("1000"), "XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }

    @Test
    void createsANewEmployeeWithSalary() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Grace", "Hopper",
                "grace@acme.com", "United States", "Engineering", "Principal Engineer",
                LocalDate.of(2019, 6, 1), new BigDecimal("185000"), "USD");
        when(employeeRepository.existsByEmail("grace@acme.com")).thenReturn(false);
        when(rateRepository.existsById("USD")).thenReturn(true);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));
        when(rateRepository.findAll())
                .thenReturn(List.of(new CurrencyRate("USD", new BigDecimal("1.00"))));

        EmployeeSummary result = service.createEmployee(request);

        assertThat(result.email()).isEqualTo("grace@acme.com");
        assertThat(result.salaryAmount()).isEqualByComparingTo("185000");
        assertThat(result.salaryUsd()).isEqualByComparingTo("185000.00");
        verify(salaryRepository).save(any(Salary.class));
    }

    @Test
    void rejectsDuplicateEmailWhenCreating() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("Grace", "Hopper",
                "grace@acme.com", "United States", "Engineering", "Principal Engineer",
                LocalDate.of(2019, 6, 1), new BigDecimal("185000"), "USD");
        when(employeeRepository.existsByEmail("grace@acme.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createEmployee(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void updatesEmployeeDetails() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Engineer", LocalDate.of(2020, 1, 1));
        Salary salary = new Salary(1L, new BigDecimal("90000"), "GBP", LocalDate.of(2020, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmailAndIdNot("ada.new@acme.com", 1L)).thenReturn(false);
        when(salaryRepository.findByEmployeeId(1L)).thenReturn(Optional.of(salary));
        when(rateRepository.findAll())
                .thenReturn(List.of(new CurrencyRate("GBP", new BigDecimal("1.27"))));

        UpdateEmployeeRequest request = new UpdateEmployeeRequest("Ada", "Byron",
                "ada.new@acme.com", "United Kingdom", "Product", "Lead");
        EmployeeSummary result = service.updateEmployee(1L, request);

        assertThat(result.lastName()).isEqualTo("Byron");
        assertThat(result.email()).isEqualTo("ada.new@acme.com");
        assertThat(result.department()).isEqualTo("Product");
        verify(employeeRepository).save(employee);
    }

    @Test
    void deactivatesEmployee() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Engineer", LocalDate.of(2020, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        service.deactivateEmployee(1L);

        assertThat(employee.isActive()).isFalse();
        verify(employeeRepository).save(employee);
    }
}

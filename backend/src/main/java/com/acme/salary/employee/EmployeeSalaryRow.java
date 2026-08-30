package com.acme.salary.employee;

import java.math.BigDecimal;

/**
 * Projection of an employee joined with their (local-currency) salary, produced
 * by the list query. The service converts {@code salaryAmount} to USD before
 * exposing it.
 */
public record EmployeeSalaryRow(
        Long id,
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobTitle,
        BigDecimal salaryAmount,
        String currency) {
}

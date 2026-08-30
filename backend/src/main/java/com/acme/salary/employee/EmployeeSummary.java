package com.acme.salary.employee;

/** A read-only view of an employee for the list screen. */
public record EmployeeSummary(
        Long id,
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobTitle) {
}

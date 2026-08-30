package com.acme.salary.seed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A generated employee together with their salary, ready to be persisted.
 * A record, so equality is by value — which lets the generator's determinism
 * be asserted directly in tests.
 */
public record SeededEmployee(
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobTitle,
        LocalDate joinDate,
        BigDecimal salaryAmount,
        String currency,
        LocalDate effectiveDate) {
}

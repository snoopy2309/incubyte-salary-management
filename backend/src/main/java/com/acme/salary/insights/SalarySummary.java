package com.acme.salary.insights;

import java.math.BigDecimal;

/**
 * Organisation-wide pay summary, all monetary values in USD.
 *
 * @param headcount    number of employees with a salary
 * @param totalUsd     total annual salary cost
 * @param averageUsd   mean salary
 * @param medianUsd    median salary (resists skew from a few high salaries)
 */
public record SalarySummary(
        long headcount,
        BigDecimal totalUsd,
        BigDecimal averageUsd,
        BigDecimal medianUsd) {
}

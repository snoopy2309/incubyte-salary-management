package com.acme.salary.insights;

import java.math.BigDecimal;

/**
 * Pay summary for one group (a country or a department), in USD.
 *
 * @param name       the country or department name
 * @param headcount  employees in the group
 * @param totalUsd   total salary cost for the group
 * @param averageUsd mean salary in the group
 * @param medianUsd  median salary in the group
 */
public record GroupSummary(
        String name,
        long headcount,
        BigDecimal totalUsd,
        BigDecimal averageUsd,
        BigDecimal medianUsd) {
}

package com.acme.salary.insights;

import java.math.BigDecimal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Read-model queries for pay insights. Uses SQL directly (via JdbcTemplate) for
 * full control over the aggregation — notably converting each salary to USD in
 * the query (amount * rate) and computing the median with {@code percentile_cont},
 * which JPQL cannot express.
 */
@Repository
public class InsightsRepository {

    private static final String SUMMARY_SQL = """
            SELECT COUNT(*)                                                             AS headcount,
                   COALESCE(SUM(s.amount * r.rate_to_usd), 0)                           AS total_usd,
                   COALESCE(AVG(s.amount * r.rate_to_usd), 0)                           AS average_usd,
                   COALESCE(percentile_cont(0.5) WITHIN GROUP (
                       ORDER BY s.amount * r.rate_to_usd), 0)                           AS median_usd
            FROM salaries s
            JOIN currency_rates r ON r.currency = s.currency
            """;

    private final JdbcTemplate jdbc;

    public InsightsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public SalarySummary fetchSummary() {
        return jdbc.queryForObject(SUMMARY_SQL, (rs, rowNum) -> new SalarySummary(
                rs.getLong("headcount"),
                rs.getBigDecimal("total_usd"),
                rs.getBigDecimal("average_usd"),
                rs.getBigDecimal("median_usd")));
    }
}

package com.acme.salary.insights;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
            JOIN employees e ON e.id = s.employee_id
            WHERE e.active = true
            """;

    /**
     * Grouped aggregation template. The {@code %s} is filled with a fixed column
     * name (never user input), so there is no injection risk.
     */
    private static final String GROUP_SQL_TEMPLATE = """
            SELECT %1$s                                                                 AS name,
                   COUNT(*)                                                             AS headcount,
                   COALESCE(SUM(s.amount * r.rate_to_usd), 0)                           AS total_usd,
                   COALESCE(AVG(s.amount * r.rate_to_usd), 0)                           AS average_usd,
                   COALESCE(percentile_cont(0.5) WITHIN GROUP (
                       ORDER BY s.amount * r.rate_to_usd), 0)                           AS median_usd
            FROM employees e
            JOIN salaries s      ON s.employee_id = e.id
            JOIN currency_rates r ON r.currency = s.currency
            WHERE e.active = true
            GROUP BY %1$s
            ORDER BY total_usd DESC
            """;

    private static final String BY_COUNTRY_SQL = GROUP_SQL_TEMPLATE.formatted("e.country");
    private static final String BY_DEPARTMENT_SQL = GROUP_SQL_TEMPLATE.formatted("e.department");

    /**
     * Counts employees per USD salary band. The band boundaries here must match
     * the labels defined in {@link InsightsService}. Bands with no employees are
     * simply absent from the result (the service fills them with zero).
     */
    private static final String DISTRIBUTION_SQL = """
            SELECT band_order, COUNT(*) AS headcount
            FROM (
                SELECT CASE
                    WHEN s.amount * r.rate_to_usd <  50000 THEN 0
                    WHEN s.amount * r.rate_to_usd <  75000 THEN 1
                    WHEN s.amount * r.rate_to_usd < 100000 THEN 2
                    WHEN s.amount * r.rate_to_usd < 150000 THEN 3
                    WHEN s.amount * r.rate_to_usd < 200000 THEN 4
                    ELSE 5
                END AS band_order
                FROM salaries s
                JOIN currency_rates r ON r.currency = s.currency
                JOIN employees e ON e.id = s.employee_id
                WHERE e.active = true
            ) banded
            GROUP BY band_order
            """;

    private static final RowMapper<GroupSummary> GROUP_MAPPER = (rs, rowNum) -> new GroupSummary(
            rs.getString("name"),
            rs.getLong("headcount"),
            rs.getBigDecimal("total_usd"),
            rs.getBigDecimal("average_usd"),
            rs.getBigDecimal("median_usd"));

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

    public List<GroupSummary> fetchByCountry() {
        return jdbc.query(BY_COUNTRY_SQL, GROUP_MAPPER);
    }

    public List<GroupSummary> fetchByDepartment() {
        return jdbc.query(BY_DEPARTMENT_SQL, GROUP_MAPPER);
    }

    /** Employee counts keyed by band order (0..5); absent bands mean zero. */
    public Map<Integer, Long> fetchDistribution() {
        Map<Integer, Long> counts = new HashMap<>();
        jdbc.query(DISTRIBUTION_SQL, rs -> {
            counts.put(rs.getInt("band_order"), rs.getLong("headcount"));
        });
        return counts;
    }
}

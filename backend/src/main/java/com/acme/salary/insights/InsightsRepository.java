package com.acme.salary.insights;

import java.util.List;
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
            GROUP BY %1$s
            ORDER BY total_usd DESC
            """;

    private static final String BY_COUNTRY_SQL = GROUP_SQL_TEMPLATE.formatted("e.country");
    private static final String BY_DEPARTMENT_SQL = GROUP_SQL_TEMPLATE.formatted("e.department");

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
}

package com.acme.salary.seed;

import com.acme.salary.currency.CurrencyRate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists generated seed data: currency rates, employees, and one salary per
 * employee. Generation is delegated to {@link SeedDataGenerator}; this class
 * writes the rows with <em>batched</em> JDBC inserts — a handful of round-trips
 * instead of one per row — so seeding 10k employees stays fast even against a
 * remote database. (Row-by-row JPA {@code saveAll} can't batch here because the
 * identity-generated ids force an insert per row to read each id back.)
 *
 * <p>Employees and salaries are given explicit ids {@code 1..count}; afterwards
 * the identity sequences are advanced past {@code count} so later application
 * inserts don't collide with the seeded ids.
 */
@Component
public class DataSeeder {

    private final JdbcTemplate jdbc;
    private final SeedDataGenerator generator = new SeedDataGenerator();

    public DataSeeder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Seed {@code count} employees (each with a salary) and the currency rates,
     * deterministically from {@code seed}. Returns the number of employees created.
     */
    @Transactional
    public int seed(int count, long seed) {
        List<CurrencyRate> rates = generator.currencyRates();
        jdbc.batchUpdate(
                "INSERT INTO currency_rates (currency, rate_to_usd) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        CurrencyRate rate = rates.get(i);
                        ps.setString(1, rate.getCurrency());
                        ps.setBigDecimal(2, rate.getRateToUsd());
                    }

                    @Override
                    public int getBatchSize() {
                        return rates.size();
                    }
                });

        List<SeededEmployee> generated = generator.generate(count, seed);

        jdbc.batchUpdate(
                "INSERT INTO employees (id, first_name, last_name, email, country, department, job_title, join_date) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        SeededEmployee e = generated.get(i);
                        ps.setLong(1, i + 1L);
                        ps.setString(2, e.firstName());
                        ps.setString(3, e.lastName());
                        ps.setString(4, e.email());
                        ps.setString(5, e.country());
                        ps.setString(6, e.department());
                        ps.setString(7, e.jobTitle());
                        ps.setObject(8, e.joinDate());
                    }

                    @Override
                    public int getBatchSize() {
                        return generated.size();
                    }
                });

        jdbc.batchUpdate(
                "INSERT INTO salaries (id, employee_id, amount, currency, effective_date) "
                        + "VALUES (?, ?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        SeededEmployee e = generated.get(i);
                        ps.setLong(1, i + 1L);
                        ps.setLong(2, i + 1L);
                        ps.setBigDecimal(3, e.salaryAmount());
                        ps.setString(4, e.currency());
                        ps.setObject(5, e.effectiveDate());
                    }

                    @Override
                    public int getBatchSize() {
                        return generated.size();
                    }
                });

        if (count > 0) {
            advanceIdentity("employees", count);
            advanceIdentity("salaries", count);
        }

        return generated.size();
    }

    /** Set the table's identity sequence so the next generated id is {@code lastUsedId + 1}. */
    private void advanceIdentity(String table, long lastUsedId) {
        jdbc.queryForObject(
                "SELECT setval(pg_get_serial_sequence(?, 'id'), ?, true)",
                Long.class, table, lastUsedId);
    }
}

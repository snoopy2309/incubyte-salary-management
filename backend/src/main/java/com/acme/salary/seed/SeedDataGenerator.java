package com.acme.salary.seed;

import com.acme.salary.currency.CurrencyRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Generates realistic, reproducible seed data: employees across several
 * countries/currencies, each with a salary in their local currency, plus the
 * matching currency rates. Pure and deterministic — the same seed always yields
 * the same data — so tests and the demo are reproducible.
 */
public class SeedDataGenerator {

    /** A country and the currency and plausible salary band (in local currency) for it. */
    private record Country(String name, String currency, BigDecimal rateToUsd,
                           int minSalary, int maxSalary) {
    }

    private static final List<Country> COUNTRIES = List.of(
            new Country("United States", "USD", new BigDecimal("1.000000"), 60_000, 200_000),
            new Country("India", "INR", new BigDecimal("0.012000"), 800_000, 4_000_000),
            new Country("United Kingdom", "GBP", new BigDecimal("1.270000"), 45_000, 140_000),
            new Country("Germany", "EUR", new BigDecimal("1.080000"), 50_000, 150_000),
            new Country("Japan", "JPY", new BigDecimal("0.006700"), 5_000_000, 15_000_000),
            new Country("Australia", "AUD", new BigDecimal("0.660000"), 70_000, 180_000));

    private static final List<String> DEPARTMENTS = List.of(
            "Engineering", "Sales", "Marketing", "Finance", "Human Resources",
            "Operations", "Product", "Customer Support");

    private static final List<String> JOB_TITLES = List.of(
            "Associate", "Specialist", "Senior Specialist", "Lead",
            "Manager", "Senior Manager", "Director");

    private static final List<String> FIRST_NAMES = List.of(
            "Ava", "Liam", "Noah", "Emma", "Olivia", "Aarav", "Diya", "Sofia", "Mateo", "Yuki",
            "Hiro", "Mia", "Lucas", "Isla", "Arjun", "Priya", "Ben", "Chloe", "Kenji", "Freya");

    private static final List<String> LAST_NAMES = List.of(
            "Smith", "Patel", "Garcia", "Muller", "Tanaka", "Brown", "Kim", "Rossi", "Nguyen", "Silva",
            "Khan", "Jones", "Sato", "Meyer", "Dubois", "Novak", "Costa", "Ali", "Wang", "Sharma");

    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 1, 1);
    private static final int MAX_TENURE_DAYS = 3650; // up to ~10 years
    private static final int SALARY_ROUNDING = 1000;

    // Pay realistically varies by seniority and by department, not just country.
    private static final Map<String, Double> ROLE_FACTOR = Map.of(
            "Associate", 0.72, "Specialist", 0.88, "Senior Specialist", 1.05, "Lead", 1.25,
            "Manager", 1.5, "Senior Manager", 1.8, "Director", 2.3);

    private static final Map<String, Double> DEPARTMENT_FACTOR = Map.of(
            "Engineering", 1.25, "Product", 1.15, "Finance", 1.1, "Sales", 1.05,
            "Marketing", 1.0, "Operations", 0.92, "Human Resources", 0.88, "Customer Support", 0.8);

    /** The currency rates backing the generated salaries. */
    public List<CurrencyRate> currencyRates() {
        List<CurrencyRate> rates = new ArrayList<>();
        for (Country country : COUNTRIES) {
            rates.add(new CurrencyRate(country.currency(), country.rateToUsd()));
        }
        return rates;
    }

    /** Generate {@code count} employees deterministically from {@code seed}. */
    public List<SeededEmployee> generate(int count, long seed) {
        Random random = new Random(seed);
        List<SeededEmployee> employees = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Country country = pick(COUNTRIES, random);
            String firstName = pick(FIRST_NAMES, random);
            String lastName = pick(LAST_NAMES, random);
            String department = pick(DEPARTMENTS, random);
            String jobTitle = pick(JOB_TITLES, random);
            // Index guarantees uniqueness regardless of name collisions.
            String email = (firstName + "." + lastName + (i + 1) + "@acme.com")
                    .toLowerCase(Locale.ROOT);
            LocalDate joinDate = REFERENCE_DATE.minusDays(random.nextInt(MAX_TENURE_DAYS));
            BigDecimal salary = randomSalary(country, department, jobTitle, random);

            employees.add(new SeededEmployee(firstName, lastName, email, country.name(),
                    department, jobTitle, joinDate, salary, country.currency(), joinDate));
        }
        return employees;
    }

    private static <T> T pick(List<T> options, Random random) {
        return options.get(random.nextInt(options.size()));
    }

    private BigDecimal randomSalary(Country country, String department, String jobTitle, Random random) {
        // A base within the country band, scaled by seniority and department, with
        // a little jitter — so pay varies realistically along all three dimensions.
        int span = country.maxSalary() - country.minSalary();
        double base = country.minSalary() + random.nextDouble() * span;
        double jitter = 0.9 + random.nextDouble() * 0.2; // ±10%
        double factored = base
                * ROLE_FACTOR.getOrDefault(jobTitle, 1.0)
                * DEPARTMENT_FACTOR.getOrDefault(department, 1.0)
                * jitter;
        long rounded = Math.round(factored / SALARY_ROUNDING) * SALARY_ROUNDING;
        return new BigDecimal(Math.max(rounded, SALARY_ROUNDING)).setScale(2);
    }
}

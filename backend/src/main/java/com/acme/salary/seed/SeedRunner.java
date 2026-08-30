package com.acme.salary.seed;

import com.acme.salary.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with 10,000 employees, then exits — a one-shot script.
 * Active only under the "seed" profile:
 *
 * <pre>./gradlew bootRun --args='--spring.profiles.active=seed'</pre>
 *
 * Idempotent: if employees already exist, it skips rather than duplicating.
 */
@Component
@Profile("seed")
public class SeedRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);
    private static final int EMPLOYEE_COUNT = 10_000;
    private static final long SEED = 20260830L;

    private final DataSeeder seeder;
    private final EmployeeRepository employeeRepository;
    private final ConfigurableApplicationContext context;

    public SeedRunner(DataSeeder seeder, EmployeeRepository employeeRepository,
                      ConfigurableApplicationContext context) {
        this.seeder = seeder;
        this.employeeRepository = employeeRepository;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        long existing = employeeRepository.count();
        if (existing > 0) {
            log.info("Database already has {} employees; skipping seed.", existing);
        } else {
            log.info("Seeding {} employees...", EMPLOYEE_COUNT);
            int created = seeder.seed(EMPLOYEE_COUNT, SEED);
            log.info("Seeded {} employees with salaries and currency rates.", created);
        }
        System.exit(SpringApplication.exit(context, () -> 0));
    }
}

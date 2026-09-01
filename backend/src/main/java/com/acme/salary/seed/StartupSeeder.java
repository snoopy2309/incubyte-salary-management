package com.acme.salary.seed;

import com.acme.salary.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database once on startup when {@code app.seed-on-startup=true}
 * (env {@code APP_SEED_ON_STARTUP}) and the database is empty. Used to populate
 * the cloud database on the first deploy; a no-op locally by default.
 */
@Component
public class StartupSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupSeeder.class);
    private static final int EMPLOYEE_COUNT = 10_000;
    private static final long SEED = 20260830L;

    private final boolean seedOnStartup;
    private final DataSeeder seeder;
    private final EmployeeRepository employeeRepository;

    public StartupSeeder(@Value("${app.seed-on-startup:false}") boolean seedOnStartup,
                         DataSeeder seeder,
                         EmployeeRepository employeeRepository) {
        this.seedOnStartup = seedOnStartup;
        this.seeder = seeder;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!seedOnStartup) {
            return;
        }
        if (employeeRepository.count() > 0) {
            log.info("Seed-on-startup enabled, but the database already has data; skipping.");
            return;
        }
        log.info("Seed-on-startup: seeding {} employees...", EMPLOYEE_COUNT);
        int created = seeder.seed(EMPLOYEE_COUNT, SEED);
        log.info("Seed-on-startup: created {} employees.", created);
    }
}

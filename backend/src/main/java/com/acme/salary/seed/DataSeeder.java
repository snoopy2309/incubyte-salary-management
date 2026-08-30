package com.acme.salary.seed;

import com.acme.salary.currency.CurrencyRateRepository;
import com.acme.salary.employee.Employee;
import com.acme.salary.employee.EmployeeRepository;
import com.acme.salary.salary.Salary;
import com.acme.salary.salary.SalaryRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists generated seed data: currency rates, employees, and one salary per
 * employee. The generation is delegated to {@link SeedDataGenerator}; this class
 * only maps to entities and saves them in one transaction.
 */
@Component
public class DataSeeder {

    private final CurrencyRateRepository rateRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final SeedDataGenerator generator = new SeedDataGenerator();

    public DataSeeder(CurrencyRateRepository rateRepository,
                      EmployeeRepository employeeRepository,
                      SalaryRepository salaryRepository) {
        this.rateRepository = rateRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
    }

    /**
     * Seed {@code count} employees (with salaries) and the currency rates,
     * deterministically from {@code seed}. Returns the number of employees created.
     */
    @Transactional
    public int seed(int count, long seed) {
        rateRepository.saveAll(generator.currencyRates());

        List<SeededEmployee> generated = generator.generate(count, seed);

        List<Employee> employees = generated.stream()
                .map(e -> new Employee(e.firstName(), e.lastName(), e.email(),
                        e.country(), e.department(), e.jobTitle(), e.joinDate()))
                .toList();
        List<Employee> savedEmployees = employeeRepository.saveAll(employees);

        List<Salary> salaries = new ArrayList<>(count);
        for (int i = 0; i < savedEmployees.size(); i++) {
            SeededEmployee source = generated.get(i);
            salaries.add(new Salary(savedEmployees.get(i).getId(),
                    source.salaryAmount(), source.currency(), source.effectiveDate()));
        }
        salaryRepository.saveAll(salaries);

        return savedEmployees.size();
    }
}

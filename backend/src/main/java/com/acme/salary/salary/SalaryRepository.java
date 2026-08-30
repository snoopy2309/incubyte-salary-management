package com.acme.salary.salary;

import org.springframework.data.jpa.repository.JpaRepository;

/** Data access for salaries. */
public interface SalaryRepository extends JpaRepository<Salary, Long> {
}

package com.acme.salary.employee;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data access for employees. Spring Data provides the CRUD and pagination
 * implementation at runtime from this interface.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Data access for employees. Spring Data provides the CRUD and pagination
 * implementation at runtime from this interface.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Employees joined with their salary, for the list screen. Ordered by id so
     * pagination is stable; the salary amount is in local currency (the service
     * converts it to USD).
     */
    @Query(value = """
            SELECT new com.acme.salary.employee.EmployeeSalaryRow(
                e.id, e.firstName, e.lastName, e.email, e.country, e.department,
                e.jobTitle, s.amount, s.currency)
            FROM Employee e, Salary s
            WHERE s.employeeId = e.id
            ORDER BY e.id
            """,
            countQuery = """
            SELECT count(e) FROM Employee e, Salary s WHERE s.employeeId = e.id
            """)
    Page<EmployeeSalaryRow> findAllWithSalary(Pageable pageable);
}

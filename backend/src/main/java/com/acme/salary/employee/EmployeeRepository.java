package com.acme.salary.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Data access for employees. Spring Data provides the CRUD and pagination
 * implementation at runtime from this interface.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    /**
     * Employees joined with their salary, for the list screen, with optional
     * filters. A null filter is ignored (matches everything). Ordered by id so
     * pagination is stable; the salary amount is in local currency (the service
     * converts it to USD).
     *
     * @param country    exact country match, or null for all
     * @param department exact department match, or null for all
     * @param q          case-insensitive search over first/last name and email, or null
     */
    @Query(value = """
            SELECT new com.acme.salary.employee.EmployeeSalaryRow(
                e.id, e.firstName, e.lastName, e.email, e.country, e.department,
                e.jobTitle, s.amount, s.currency)
            FROM Employee e, Salary s
            WHERE s.employeeId = e.id
              AND (:country IS NULL OR e.country = :country)
              AND (:department IS NULL OR e.department = :department)
              AND (:q IS NULL
                   OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                   OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                   OR LOWER(e.email)     LIKE LOWER(CONCAT('%', CAST(:q AS string), '%')))
            ORDER BY e.id
            """,
            countQuery = """
            SELECT count(e)
            FROM Employee e, Salary s
            WHERE s.employeeId = e.id
              AND (:country IS NULL OR e.country = :country)
              AND (:department IS NULL OR e.department = :department)
              AND (:q IS NULL
                   OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                   OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', CAST(:q AS string), '%'))
                   OR LOWER(e.email)     LIKE LOWER(CONCAT('%', CAST(:q AS string), '%')))
            """)
    Page<EmployeeSalaryRow> findAllWithSalary(
            @Param("country") String country,
            @Param("department") String department,
            @Param("q") String q,
            Pageable pageable);
}

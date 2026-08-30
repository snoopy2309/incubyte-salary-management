package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application logic for browsing employees. */
@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<EmployeeSummary> list(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable).map(EmployeeService::toSummary));
    }

    private static EmployeeSummary toSummary(Employee employee) {
        return new EmployeeSummary(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getCountry(),
                employee.getDepartment(),
                employee.getJobTitle());
    }
}

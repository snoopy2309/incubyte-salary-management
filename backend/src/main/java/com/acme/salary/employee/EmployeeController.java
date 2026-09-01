package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for browsing employees. */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /**
     * List employees, paginated, with optional filters.
     * Query params: {@code country}, {@code department}, {@code q} (search over
     * name/email), plus {@code page} and {@code size}.
     */
    @GetMapping
    public PagedResponse<EmployeeSummary> list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return service.list(country, department, q, pageable);
    }

    /** Add a new employee together with their salary. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeSummary create(@Valid @RequestBody CreateEmployeeRequest request) {
        return service.createEmployee(request);
    }

    /** Update an employee's salary (amount and/or currency). */
    @PatchMapping("/{id}/salary")
    public EmployeeSummary updateSalary(@PathVariable Long id,
                                        @Valid @RequestBody UpdateSalaryRequest request) {
        return service.updateSalary(id, request.amount(), request.currency());
    }

    /** Update an employee's personal/organisational details. */
    @PatchMapping("/{id}")
    public EmployeeSummary update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateEmployeeRequest request) {
        return service.updateEmployee(id, request);
    }

    /** Deactivate (soft-delete) an employee. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivateEmployee(id);
    }
}

package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}

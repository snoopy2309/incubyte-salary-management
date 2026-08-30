package com.acme.salary.employee;

import com.acme.salary.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for browsing employees. */
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    /**
     * List employees, paginated. Query params: {@code page}, {@code size},
     * {@code sort} (e.g. {@code ?page=0&size=20&sort=country,asc}).
     */
    @GetMapping
    public PagedResponse<EmployeeSummary> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }
}

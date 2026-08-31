package com.acme.salary.insights;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints answering questions about how the organisation pays people. */
@RestController
@RequestMapping("/api/v1/insights")
public class InsightsController {

    private final InsightsService service;

    public InsightsController(InsightsService service) {
        this.service = service;
    }

    /** Organisation-wide pay summary (USD): headcount, total, average, median. */
    @GetMapping("/summary")
    public SalarySummary summary() {
        return service.summary();
    }

    /** Pay per country (USD): headcount, total, average, median. */
    @GetMapping("/by-country")
    public List<GroupSummary> byCountry() {
        return service.byCountry();
    }

    /** Pay per department (USD): headcount, total, average, median. */
    @GetMapping("/by-department")
    public List<GroupSummary> byDepartment() {
        return service.byDepartment();
    }

    /** Salary distribution: employee counts per USD pay band. */
    @GetMapping("/distribution")
    public List<SalaryBand> distribution() {
        return service.distribution();
    }
}

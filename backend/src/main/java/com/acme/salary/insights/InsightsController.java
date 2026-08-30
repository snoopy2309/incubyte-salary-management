package com.acme.salary.insights;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints answering questions about how the organisation pays people. */
@RestController
@RequestMapping("/insights")
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
}

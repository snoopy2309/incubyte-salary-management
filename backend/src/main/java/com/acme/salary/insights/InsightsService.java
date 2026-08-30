package com.acme.salary.insights;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application logic for pay insights: reads the aggregates and rounds for display. */
@Service
public class InsightsService {

    private static final int MONEY_SCALE = 2;

    private final InsightsRepository repository;

    public InsightsService(InsightsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public SalarySummary summary() {
        SalarySummary raw = repository.fetchSummary();
        return new SalarySummary(
                raw.headcount(),
                round(raw.totalUsd()),
                round(raw.averageUsd()),
                round(raw.medianUsd()));
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}

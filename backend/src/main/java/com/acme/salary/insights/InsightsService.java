package com.acme.salary.insights;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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

    @Transactional(readOnly = true)
    public List<GroupSummary> byCountry() {
        return rounded(repository.fetchByCountry());
    }

    @Transactional(readOnly = true)
    public List<GroupSummary> byDepartment() {
        return rounded(repository.fetchByDepartment());
    }

    private static List<GroupSummary> rounded(List<GroupSummary> groups) {
        return groups.stream()
                .map(g -> new GroupSummary(g.name(), g.headcount(),
                        round(g.totalUsd()), round(g.averageUsd()), round(g.medianUsd())))
                .toList();
    }

    private static BigDecimal round(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}

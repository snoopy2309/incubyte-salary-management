package com.acme.salary.insights;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Application logic for pay insights: reads the aggregates and rounds for display. */
@Service
public class InsightsService {

    private static final int MONEY_SCALE = 2;

    /** Band order -> label; boundaries defined in InsightsRepository's distribution query. */
    private static final List<Band> BANDS = List.of(
            new Band(0, "< $50k"),
            new Band(1, "$50k–$75k"),
            new Band(2, "$75k–$100k"),
            new Band(3, "$100k–$150k"),
            new Band(4, "$150k–$200k"),
            new Band(5, "$200k+"));

    private record Band(int order, String label) {
    }

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

    @Transactional(readOnly = true)
    public List<SalaryBand> distribution() {
        Map<Integer, Long> counts = repository.fetchDistribution();
        return BANDS.stream()
                .map(band -> new SalaryBand(band.label(), counts.getOrDefault(band.order(), 0L)))
                .toList();
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

package com.acme.salary.insights;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit test: the service rounds the raw aggregates to two decimal places. */
@ExtendWith(MockitoExtension.class)
class InsightsServiceTest {

    @Mock
    private InsightsRepository repository;

    @InjectMocks
    private InsightsService service;

    @Test
    void roundsSummaryValuesToTwoDecimalPlaces() {
        when(repository.fetchSummary()).thenReturn(new SalarySummary(
                3,
                new BigDecimal("100000.129"),
                new BigDecimal("33333.3333"),
                new BigDecimal("30000.005")));

        SalarySummary summary = service.summary();

        assertThat(summary.headcount()).isEqualTo(3);
        assertThat(summary.totalUsd()).isEqualByComparingTo("100000.13");
        assertThat(summary.averageUsd()).isEqualByComparingTo("33333.33");
        assertThat(summary.medianUsd()).isEqualByComparingTo("30000.01");
    }

    @Test
    void roundsGroupedValuesToTwoDecimalPlaces() {
        when(repository.fetchByCountry()).thenReturn(List.of(new GroupSummary(
                "India", 1,
                new BigDecimal("12000.129"),
                new BigDecimal("12000.129"),
                new BigDecimal("12000.125"))));

        List<GroupSummary> result = service.byCountry();

        assertThat(result).hasSize(1);
        GroupSummary india = result.get(0);
        assertThat(india.name()).isEqualTo("India");
        assertThat(india.totalUsd()).isEqualByComparingTo("12000.13");
        assertThat(india.averageUsd()).isEqualByComparingTo("12000.13");
        assertThat(india.medianUsd()).isEqualByComparingTo("12000.13");
    }

    @Test
    void distributionReturnsAllBandsFillingEmptyOnesWithZero() {
        when(repository.fetchDistribution()).thenReturn(Map.of(2, 2L));

        List<SalaryBand> bands = service.distribution();

        assertThat(bands).hasSize(6);
        assertThat(bands.get(0).label()).isEqualTo("< $50k");
        assertThat(bands.get(0).headcount()).isZero();
        assertThat(bands.get(2).label()).isEqualTo("$75k–$100k");
        assertThat(bands.get(2).headcount()).isEqualTo(2);
    }
}

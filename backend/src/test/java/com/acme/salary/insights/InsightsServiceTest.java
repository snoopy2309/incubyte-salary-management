package com.acme.salary.insights;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
}

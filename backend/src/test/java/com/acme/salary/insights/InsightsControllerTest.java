package com.acme.salary.insights;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Web-layer test: GET /insights/summary returns the summary JSON. */
@WebMvcTest(InsightsController.class)
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsightsService service;

    @Test
    void returnsSalarySummary() throws Exception {
        when(service.summary()).thenReturn(new SalarySummary(
                10000,
                new BigDecimal("482600000.00"),
                new BigDecimal("84200.00"),
                new BigDecimal("71500.00")));

        mockMvc.perform(get("/insights/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headcount").value(10000))
                .andExpect(jsonPath("$.totalUsd").value(482600000.00))
                .andExpect(jsonPath("$.averageUsd").value(84200.00))
                .andExpect(jsonPath("$.medianUsd").value(71500.00));
    }

    @Test
    void returnsPayByCountry() throws Exception {
        when(service.byCountry()).thenReturn(List.of(new GroupSummary(
                "United States", 1688,
                new BigDecimal("200000000.00"),
                new BigDecimal("118000.00"),
                new BigDecimal("110000.00"))));

        mockMvc.perform(get("/insights/by-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("United States"))
                .andExpect(jsonPath("$[0].headcount").value(1688))
                .andExpect(jsonPath("$[0].medianUsd").value(110000.00));
    }

    @Test
    void returnsPayByDepartment() throws Exception {
        when(service.byDepartment()).thenReturn(List.of(new GroupSummary(
                "Engineering", 1300,
                new BigDecimal("150000000.00"),
                new BigDecimal("115000.00"),
                new BigDecimal("108000.00"))));

        mockMvc.perform(get("/insights/by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].headcount").value(1300));
    }
}

package com.acme.salary.insights;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
}

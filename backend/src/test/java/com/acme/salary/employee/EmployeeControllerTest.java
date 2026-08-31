package com.acme.salary.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acme.salary.common.PagedResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** Web-layer test: GET /employees returns the paginated JSON contract. */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService service;

    @Test
    void returnsPagedEmployees() throws Exception {
        EmployeeSummary summary = new EmployeeSummary(1L, "Ada", "Lovelace",
                "ada@acme.com", "United Kingdom", "Engineering", "Software Engineer",
                new BigDecimal("90000.00"), "GBP", new BigDecimal("114300.00"));
        when(service.list(nullable(String.class), nullable(String.class), nullable(String.class),
                any(Pageable.class)))
                .thenReturn(new PagedResponse<>(List.of(summary), 0, 20, 1, 1,
                        false, false, true, true));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("ada@acme.com"))
                .andExpect(jsonPath("$.content[0].department").value("Engineering"))
                .andExpect(jsonPath("$.content[0].salaryUsd").value(114300.00))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }
}

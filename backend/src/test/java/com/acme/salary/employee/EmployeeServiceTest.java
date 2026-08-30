package com.acme.salary.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.acme.salary.common.PagedResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/** Unit test: the service maps employee entities to summary DTOs, page by page. */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService service;

    @Test
    void listsEmployeesAsSummaries() {
        Employee employee = new Employee("Ada", "Lovelace", "ada@acme.com",
                "United Kingdom", "Engineering", "Software Engineer", LocalDate.of(2020, 1, 1));
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(employee)));

        PagedResponse<EmployeeSummary> result = service.list(PageRequest.of(0, 20));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).email()).isEqualTo("ada@acme.com");
        assertThat(result.content().get(0).department()).isEqualTo("Engineering");
        assertThat(result.totalElements()).isEqualTo(1);
    }
}

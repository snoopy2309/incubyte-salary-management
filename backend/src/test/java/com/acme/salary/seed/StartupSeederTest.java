package com.acme.salary.seed;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.acme.salary.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartupSeederTest {

    @Mock
    private DataSeeder seeder;

    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void seedsWhenEnabledAndEmpty() {
        when(employeeRepository.count()).thenReturn(0L);

        new StartupSeeder(true, seeder, employeeRepository).run(null);

        verify(seeder).seed(anyInt(), anyLong());
    }

    @Test
    void doesNothingWhenDisabled() {
        new StartupSeeder(false, seeder, employeeRepository).run(null);

        verifyNoInteractions(seeder);
    }

    @Test
    void skipsWhenDatabaseAlreadyPopulated() {
        when(employeeRepository.count()).thenReturn(5L);

        new StartupSeeder(true, seeder, employeeRepository).run(null);

        verify(seeder, never()).seed(anyInt(), anyLong());
    }
}

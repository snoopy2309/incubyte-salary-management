package com.acme.salary.employee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body to add a new employee together with their salary. */
public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String country,
        @NotBlank String department,
        @NotBlank String jobTitle,
        @NotNull LocalDate joinDate,
        @NotNull @DecimalMin("0.01") BigDecimal salaryAmount,
        @NotBlank @Size(min = 3, max = 3) String currency) {
}

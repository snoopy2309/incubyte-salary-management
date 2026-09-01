package com.acme.salary.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request body to update an employee's personal/organisational details. */
public record UpdateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String country,
        @NotBlank String department,
        @NotBlank String jobTitle) {
}

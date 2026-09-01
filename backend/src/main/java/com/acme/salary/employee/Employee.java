package com.acme.salary.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * An employee and the organisational attributes the insights slice by
 * (country, department, job title). Maps to the {@code employees} table.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String department;
    private String jobTitle;
    private LocalDate joinDate;
    private boolean active = true;

    /** Required by JPA. */
    protected Employee() {
    }

    public Employee(String firstName, String lastName, String email, String country,
                    String department, String jobTitle, LocalDate joinDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.department = department;
        this.jobTitle = jobTitle;
        this.joinDate = joinDate;
    }

    /** Update the editable personal/organisational details. */
    public void updateDetails(String firstName, String lastName, String email,
                              String country, String department, String jobTitle) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.department = department;
        this.jobTitle = jobTitle;
    }

    /** Soft-delete: mark inactive rather than removing the record. */
    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }
}

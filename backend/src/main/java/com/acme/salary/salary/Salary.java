package com.acme.salary.salary;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * An employee's salary, held in their local currency with an effective date.
 * v1 uses one current salary per employee; the effective date leaves room for
 * salary history later without a schema redesign.
 *
 * <p>The link to the employee is kept as a foreign-key id (not a JPA
 * association) because listing and insights use explicit joins, which avoids
 * lazy-loading surprises and N+1 queries.
 */
@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private BigDecimal amount;
    private String currency;
    private LocalDate effectiveDate;

    /** Required by JPA. */
    protected Salary() {
    }

    public Salary(Long employeeId, BigDecimal amount, String currency, LocalDate effectiveDate) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.currency = currency;
        this.effectiveDate = effectiveDate;
    }

    /** Update the salary to a new amount and currency (v1 keeps one current salary). */
    public void changeTo(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
}

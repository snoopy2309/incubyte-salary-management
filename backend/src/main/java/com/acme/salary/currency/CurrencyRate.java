package com.acme.salary.currency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Reference data: how many USD one unit of a currency is worth (USD is 1.0).
 * Maps to the {@code currency_rates} table; the currency code is the id.
 */
@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    @Id
    private String currency;

    private BigDecimal rateToUsd;

    /** Required by JPA. */
    protected CurrencyRate() {
    }

    public CurrencyRate(String currency, BigDecimal rateToUsd) {
        this.currency = currency;
        this.rateToUsd = rateToUsd;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getRateToUsd() {
        return rateToUsd;
    }
}

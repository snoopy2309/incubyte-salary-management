package com.acme.salary.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Converts monetary amounts from a local currency to the USD base used for all
 * cross-country comparisons and aggregations.
 *
 * <p>Rates are "USD per one unit of the currency" (USD is 1.00). Kept as pure
 * logic with an injected rate table so it is trivial to test and free of any
 * database or framework dependency.
 */
public class CurrencyConverter {

    private static final int USD_SCALE = 2;

    private final Map<String, BigDecimal> ratesToUsd;

    public CurrencyConverter(Map<String, BigDecimal> ratesToUsd) {
        this.ratesToUsd = Map.copyOf(ratesToUsd);
    }

    /**
     * Convert {@code amount}, expressed in {@code currencyCode}, into USD,
     * rounded to two decimal places (half-up).
     *
     * @throws IllegalArgumentException if the currency has no known rate
     */
    public BigDecimal toUsd(BigDecimal amount, String currencyCode) {
        BigDecimal rate = ratesToUsd.get(currencyCode);
        if (rate == null) {
            throw new IllegalArgumentException("Unknown currency: " + currencyCode);
        }
        return amount.multiply(rate).setScale(USD_SCALE, RoundingMode.HALF_UP);
    }
}

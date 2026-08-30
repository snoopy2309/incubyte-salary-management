package com.acme.salary.currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * The core of the product: converting salaries from their local currency to a
 * common USD base so figures across countries can be compared and aggregated.
 *
 * Pure logic — no Spring, no database — so these tests are fast and deterministic.
 * A rate is "how many USD one unit of the currency is worth" (USD itself is 1.00).
 */
class CurrencyConverterTest {

    private final CurrencyConverter converter = new CurrencyConverter(Map.of(
            "USD", new BigDecimal("1.00"),
            "INR", new BigDecimal("0.012"),
            "EUR", new BigDecimal("1.08")));

    @Test
    void usdAmountIsReturnedUnchanged() {
        assertThat(converter.toUsd(new BigDecimal("1000"), "USD"))
                .isEqualByComparingTo("1000.00");
    }

    @Test
    void convertsLocalCurrencyToUsdUsingItsRate() {
        // 1,000,000 INR * 0.012 = 12,000 USD
        assertThat(converter.toUsd(new BigDecimal("1000000"), "INR"))
                .isEqualByComparingTo("12000.00");
    }

    @Test
    void roundsResultToTwoDecimalPlaces() {
        // 999 EUR * 1.08 = 1078.92
        assertThat(converter.toUsd(new BigDecimal("999"), "EUR"))
                .isEqualByComparingTo("1078.92");
    }

    @Test
    void rejectsUnknownCurrency() {
        assertThatThrownBy(() -> converter.toUsd(new BigDecimal("100"), "XYZ"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }
}

package com.acme.salary.seed;

import static org.assertj.core.api.Assertions.assertThat;

import com.acme.salary.currency.CurrencyRate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * The seed generator is pure (no database), so its guarantees — count,
 * determinism, valid currencies, unique emails — are proven with fast unit
 * tests. Determinism matters so the demo and any data-dependent behaviour are
 * reproducible.
 */
class SeedDataGeneratorTest {

    private final SeedDataGenerator generator = new SeedDataGenerator();

    @Test
    void generatesTheRequestedNumberOfEmployees() {
        assertThat(generator.generate(100, 1L)).hasSize(100);
    }

    @Test
    void isDeterministicForTheSameSeed() {
        assertThat(generator.generate(50, 42L)).isEqualTo(generator.generate(50, 42L));
    }

    @Test
    void differentSeedsProduceDifferentData() {
        assertThat(generator.generate(50, 1L)).isNotEqualTo(generator.generate(50, 2L));
    }

    @Test
    void everyEmployeeHasAPositiveSalaryInAKnownCurrency() {
        Set<String> knownCurrencies = generator.currencyRates().stream()
                .map(CurrencyRate::getCurrency)
                .collect(Collectors.toSet());

        List<SeededEmployee> employees = generator.generate(500, 7L);

        assertThat(employees).isNotEmpty().allSatisfy(employee -> {
            assertThat(employee.currency()).isIn(knownCurrencies);
            assertThat(employee.salaryAmount()).isGreaterThan(BigDecimal.ZERO);
        });
    }

    @Test
    void generatesUniqueEmails() {
        List<SeededEmployee> employees = generator.generate(1000, 3L);

        Set<String> emails = employees.stream()
                .map(SeededEmployee::email)
                .collect(Collectors.toSet());

        assertThat(emails).hasSize(1000);
    }

    @Test
    void providesARateForEveryCurrencyItUses() {
        Set<String> rateCurrencies = generator.currencyRates().stream()
                .map(CurrencyRate::getCurrency)
                .collect(Collectors.toSet());

        Set<String> usedCurrencies = generator.generate(500, 9L).stream()
                .map(SeededEmployee::currency)
                .collect(Collectors.toSet());

        assertThat(rateCurrencies).containsAll(usedCurrencies);
    }
}

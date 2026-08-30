package com.acme.salary.currency;

import org.springframework.data.jpa.repository.JpaRepository;

/** Data access for currency rates, keyed by currency code. */
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, String> {
}

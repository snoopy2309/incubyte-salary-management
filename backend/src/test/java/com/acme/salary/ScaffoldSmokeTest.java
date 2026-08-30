package com.acme.salary;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * A plain unit test (no Spring context, no database) that proves the build and
 * test wiring work. Real behaviour is driven test-first from here on.
 */
class ScaffoldSmokeTest {

    @Test
    void buildAndTestHarnessRun() {
        assertThat(1 + 1).isEqualTo(2);
    }
}

package com.acme.salary.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/** Unit test: exceptions map to the right status and a safe error body. */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsIllegalArgumentToBadRequestWithMessage() {
        ResponseEntity<ErrorResponse> response =
                handler.handleBadRequest(new IllegalArgumentException("Unknown currency: XYZ"));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).contains("XYZ");
    }

    @Test
    void mapsUnexpectedExceptionToInternalServerErrorWithGenericMessage() {
        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(new RuntimeException("some internal detail"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        // Internal detail must not leak to the client.
        assertThat(response.getBody().message()).doesNotContain("some internal detail");
    }
}

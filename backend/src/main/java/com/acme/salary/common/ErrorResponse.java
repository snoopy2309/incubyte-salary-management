package com.acme.salary.common;

/** A consistent JSON error body returned by the API. */
public record ErrorResponse(int status, String error, String message) {
}

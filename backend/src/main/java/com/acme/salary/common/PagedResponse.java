package com.acme.salary.common;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * A stable, explicit JSON shape for paginated responses, so the API contract
 * does not depend on Spring's internal Page serialisation.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}

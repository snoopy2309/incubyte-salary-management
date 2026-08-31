package com.acme.salary.common;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * A stable, explicit JSON shape for paginated responses, so the API contract
 * does not depend on Spring's internal Page serialisation.
 *
 * <p>Offset pagination: {@code totalElements}/{@code totalPages} give the HR user
 * a count and jump-to-page, while {@code hasNext}/{@code hasPrevious} drive the
 * navigation buttons. See docs/trade-offs.md for the scaling path (keyset).
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean first,
        boolean last) {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast());
    }
}

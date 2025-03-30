package com.alvaro.empresas.passagens.dtos;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageOutput<T>(
        int nPage,
        int size,
        int nTotalPages,
        boolean hasNextPage,
        long nTotalElements,
        List<T> content
) {
    public PageOutput(Page<T> page) {
        this(page.getPageable().getPageNumber(),
                page.getPageable().getPageSize(),
                page.getTotalPages(),
                page.getPageable().getPageNumber() < page.getTotalPages() - 1,
                page.getTotalElements(),
                page.getContent());
    }
}

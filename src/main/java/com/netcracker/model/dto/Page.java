package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page<T> {
    private Integer pageSize;
    private Integer pageNumber;
    private Long totalElements;
    private Long totalPages;
    private Integer realSize;
    private Collection<T> data;

    public Page() {
    }

    public Page(Integer pageSize, Integer pageNumber, Long totalElements, Collection<T> data) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalElements = totalElements;
        this.totalPages = (totalElements / pageSize)+1;
        this.data = data;
        this.realSize = data.size();
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public Collection<T> getData() {
        return data;
    }

    public Integer getRealSize() {
        return realSize;
    }
}

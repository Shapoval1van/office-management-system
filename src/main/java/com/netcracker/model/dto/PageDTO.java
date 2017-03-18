package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDTO<T> {
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalElements;
    private Integer totalPages;
    private Collection<T> data;

    public PageDTO() {
    }

    public PageDTO(Integer pageSize, Integer pageNumber, Integer totalElements, Collection<T> data) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalElements = totalElements;
        this.totalPages = (totalElements / pageSize)+1;
        this.data = data;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Collection<T> getData() {
        return data;
    }
}

package com.netcracker.repository.common;


public interface Pageable {
    int getPageSize();
    int getPageNumber();
    String getSort();
}
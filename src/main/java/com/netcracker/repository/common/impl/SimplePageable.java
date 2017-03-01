package com.netcracker.repository.common.impl;


import com.netcracker.repository.common.Pageable;

public class SimplePageable implements Pageable {

    public final static int DEFAULT_PAGE_SIZE = 25;
    public final static int DEFAULT_PAGE_NUMBER = 1;

    private int pageSize;
    private int pageNumber;

    public SimplePageable() {
    }

    public SimplePageable(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber - 1;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber - 1;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }
}

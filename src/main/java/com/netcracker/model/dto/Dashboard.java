package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dashboard {

    //employee rows
    private Integer requestListSize;
    private Integer freeRequestListCount;
    private Integer progressRequestListCount;
    private Integer closedRequestListCount;
    private Integer canceledRequestListCount;
    //manager rows
    private Integer freeAssignedRequestCount;
    private Integer progressAssignedRequestCount;
    private Integer closedAssignedRequestCount;
    //admin rows
    private Integer freeRequestListNoManager;
    private Integer allRequestListSize;
    private Integer allFreeRequestCount;
    private Integer allProgressRequestCount;
    private Integer allClosedRequestCount;
    private Integer allCancelRequestCount;
    private Integer personListSize;
    private Integer adminCount;
    private Integer managerCount;
    private Integer employeeCount;

    public Dashboard(){}

    public Dashboard(Integer requestListSize,
                     Integer freeRequestListCount,
                     Integer progressRequestListCount,
                     Integer closedRequestListCount,
                     Integer canceledRequestListCount) {
        this.requestListSize = requestListSize;
        this.freeRequestListCount = freeRequestListCount;
        this.progressRequestListCount = progressRequestListCount;
        this.closedRequestListCount = closedRequestListCount;
        this.canceledRequestListCount = canceledRequestListCount;
    }

    public Dashboard(Integer requestListSize,
                     Integer freeRequestListCount,
                     Integer progressRequestListCount,
                     Integer closedRequestListCount,
                     Integer canceledRequestListCount,
                     Integer freeAssignedRequestCount,
                     Integer progressAssignedRequestCount,
                     Integer closedAssignedRequestCount) {
        this.requestListSize = requestListSize;
        this.freeRequestListCount = freeRequestListCount;
        this.progressRequestListCount = progressRequestListCount;
        this.closedRequestListCount = closedRequestListCount;
        this.canceledRequestListCount = canceledRequestListCount;
        this.freeAssignedRequestCount = freeAssignedRequestCount;
        this.progressAssignedRequestCount = progressAssignedRequestCount;
        this.closedAssignedRequestCount = closedAssignedRequestCount;
    }

    public Dashboard(Integer requestListSize,
                     Integer freeRequestListCount,
                     Integer progressRequestListCount,
                     Integer closedRequestListCount,
                     Integer canceledRequestListCount,
                     Integer freeAssignedRequestCount,
                     Integer progressAssignedRequestCount,
                     Integer closedAssignedRequestCount,
                     Integer freeRequestListNoManager,
                     Integer allRequestListSize,
                     Integer allFreeRequestCount,
                     Integer allProgressRequestCount,
                     Integer allClosedRequestCount,
                     Integer allCancelRequestCount,
                     Integer personListSize,
                     Integer adminCount,
                     Integer managerCount,
                     Integer employeeCount) {
        this.requestListSize = requestListSize;
        this.freeRequestListCount = freeRequestListCount;
        this.progressRequestListCount = progressRequestListCount;
        this.closedRequestListCount = closedRequestListCount;
        this.canceledRequestListCount = canceledRequestListCount;
        this.freeAssignedRequestCount = freeAssignedRequestCount;
        this.progressAssignedRequestCount = progressAssignedRequestCount;
        this.closedAssignedRequestCount = closedAssignedRequestCount;
        this.freeRequestListNoManager = freeRequestListNoManager;
        this.allRequestListSize = allRequestListSize;
        this.allFreeRequestCount = allFreeRequestCount;
        this.allProgressRequestCount = allProgressRequestCount;
        this.allClosedRequestCount = allClosedRequestCount;
        this.allCancelRequestCount = allCancelRequestCount;
        this.personListSize = personListSize;
        this.adminCount = adminCount;
        this.managerCount = managerCount;
        this.employeeCount = employeeCount;
    }

    public Integer getFreeAssignedRequestCount() {
        return freeAssignedRequestCount;
    }

    public void setFreeAssignedRequestCount(Integer freeAssignedRequestCount) {
        this.freeAssignedRequestCount = freeAssignedRequestCount;
    }

    public Integer getProgressAssignedRequestCount() {
        return progressAssignedRequestCount;
    }

    public void setProgressAssignedRequestCount(Integer progressAssignedRequestCount) {
        this.progressAssignedRequestCount = progressAssignedRequestCount;
    }

    public Integer getClosedAssignedRequestCount() {
        return closedAssignedRequestCount;
    }

    public void setClosedAssignedRequestCount(Integer closedAssignedRequestCount) {
        this.closedAssignedRequestCount = closedAssignedRequestCount;
    }

    public Integer getFreeRequestListNoManager() {
        return freeRequestListNoManager;
    }

    public void setFreeRequestListNoManager(Integer freeRequestListNoManager) {
        this.freeRequestListNoManager = freeRequestListNoManager;
    }

    public Integer getAllRequestListSize() {
        return allRequestListSize;
    }

    public void setAllRequestListSize(Integer allRequestListSize) {
        this.allRequestListSize = allRequestListSize;
    }

    public Integer getAllFreeRequestCount() {
        return allFreeRequestCount;
    }

    public void setAllFreeRequestCount(Integer allFreeRequestCount) {
        this.allFreeRequestCount = allFreeRequestCount;
    }

    public Integer getAllProgressRequestCount() {
        return allProgressRequestCount;
    }

    public void setAllProgressRequestCount(Integer allProgressRequestCount) {
        this.allProgressRequestCount = allProgressRequestCount;
    }

    public Integer getAllClosedRequestCount() {
        return allClosedRequestCount;
    }

    public void setAllClosedRequestCount(Integer allClosedRequestCount) {
        this.allClosedRequestCount = allClosedRequestCount;
    }

    public Integer getAllCancelRequestCount() {
        return allCancelRequestCount;
    }

    public void setAllCancelRequestCount(Integer allCancelRequestCount) {
        this.allCancelRequestCount = allCancelRequestCount;
    }

    public Integer getPersonListSize() {
        return personListSize;
    }

    public void setPersonListSize(Integer personListSize) {
        this.personListSize = personListSize;
    }

    public Integer getAdminCount() {
        return adminCount;
    }

    public void setAdminCount(Integer adminCount) {
        this.adminCount = adminCount;
    }

    public Integer getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(Integer managerCount) {
        this.managerCount = managerCount;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Integer getRequestListSize() {
        return requestListSize;
    }

    public void setRequestListSize(Integer requestListSize) {
        this.requestListSize = requestListSize;
    }

    public Integer getFreeRequestListCount() {
        return freeRequestListCount;
    }

    public void setFreeRequestListCount(Integer freeRequestListCount) {
        this.freeRequestListCount = freeRequestListCount;
    }

    public Integer getProgressRequestListCount() {
        return progressRequestListCount;
    }

    public void setProgressRequestListCount(Integer progressRequestListCount) {
        this.progressRequestListCount = progressRequestListCount;
    }

    public Integer getClosedRequestListCount() {
        return closedRequestListCount;
    }

    public void setClosedRequestListCount(Integer closedRequestListCount) {
        this.closedRequestListCount = closedRequestListCount;
    }

    public Integer getCanceledRequestListCount() {
        return canceledRequestListCount;
    }

    public void setCanceledRequestListCount(Integer canceledRequestListCount) {
        this.canceledRequestListCount = canceledRequestListCount;
    }
}

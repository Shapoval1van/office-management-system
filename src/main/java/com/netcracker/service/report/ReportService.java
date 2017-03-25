package com.netcracker.service.report;


import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.ChartsType;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface ReportService {
    List<Request> getAllRequestByAdminForPeriodWithAlternativeRole(Long personId, String period, Long alternativeRoleId)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<Request> getAllRequestByAdminForPeriodWithAlternativeRole(Long personId, String period, Long alternativeRoleId, Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<ReportDTO> getDataForChartsToManager(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<ReportDTO> getDataForChartsToEmployee(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<ReportDTO> getDataForChartsToAdmin(Long personId, String period, ChartsType chartsType, Long alternativeRoleId)
            throws CurrentUserNotPresentException;
    Long countRequestByPersonIdForPeriod(Long personId, String reportPeriod) throws CurrentUserNotPresentException;
    Long countRequestByAdminIdForPeriod(Long personId, String reportPeriod, Long alternativeRoleId);
}

package com.netcracker.service.report;


import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.ChartsType;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface ReportService {
    List<Request> getAllRequestByAdminForPeriodWithAlternativeRole(Long personId, String period, Role alternativeRole)
            throws CurrentUserNotPresentException;
    List<Request> getAllRequestByAdminForPeriodWithAlternativeRole(Long personId, String period, Role alternativeRole, Pageable pageable)
            throws CurrentUserNotPresentException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period) throws CurrentUserNotPresentException;
    List<ReportDTO> getDataForChartsToManager(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<ReportDTO> getDataForChartsToEmployee(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<ReportDTO> getDataForChartsToAdmin(Long personId, String period, ChartsType chartsType, Role alternativeRole)
            throws CurrentUserNotPresentException;
    Long countRequestByPersonIdForPeriod(Long personId, String reportPeriod);
    Long countRequestByAdminIdForPeriod(Long personId, String reportPeriod, Role alternativeRole);
}

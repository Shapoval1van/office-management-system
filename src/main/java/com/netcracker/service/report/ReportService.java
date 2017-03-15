package com.netcracker.service.report;


import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.ChartsType;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface ReportService {
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable) throws CurrentUserNotPresentException, NotDataForThisRoleException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period) throws CurrentUserNotPresentException;
    List<ReportDTO> getDataForChartsToManager(Long personId, String period, ChartsType chartsType) throws CurrentUserNotPresentException, NotDataForThisRoleException;
}

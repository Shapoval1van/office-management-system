package com.netcracker.service.report;


import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface ReportService {
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable) throws CurrentUserNotPresentException;
    List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period) throws CurrentUserNotPresentException;
}

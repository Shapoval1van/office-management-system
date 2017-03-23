package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.CalendarItemDTO;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by nuts on 3/19/17.
 */
public interface CalendarService {
    List<CalendarItemDTO> getDataByPeriod(Timestamp start, Timestamp end, Principal principal) throws CurrentUserNotPresentException;
}

package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.CalendarItemDto;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by nuts on 3/19/17.
 */
public interface CalendarService {
    List<CalendarItemDto> getDataByPeriod(Timestamp start, Timestamp end, String email) throws CurrentUserNotPresentException;
}

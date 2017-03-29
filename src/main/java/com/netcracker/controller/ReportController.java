
package com.netcracker.controller;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.CalendarItemDTO;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.ChartsType;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.report.CalendarService;
import com.netcracker.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@Validated
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private CalendarService calendarService;

    private static final String JSON_MEDIA_TYPE = "application/json;";

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/allRequest/{personId}")
    public List<FullRequestDTO> getAllRequestByPersonForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                               @RequestParam(name = "period", defaultValue = "month") String period,
                                                               @PathVariable(name = "personId") Long personId, Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        List<FullRequestDTO> responseList = new ArrayList<>();
        reportService.getAllRequestByPersonIdForPeriod(personId, period, pageable).forEach(request -> responseList.add(new FullRequestDTO(request)));
        return responseList;
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/admin/allRequest/{personId}")
    public List<FullRequestDTO> getAllRequestByAdminForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                               @RequestParam(name = "period", defaultValue = "month") String period,
                                                               @PathVariable(name = "personId") Long personId,
                                                               @Pattern(regexp = "(2|3)")
                                                               @RequestParam(name = "role", defaultValue = "2") String roleId,
                                                               Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        List<FullRequestDTO> responseList = new ArrayList<>();
        reportService.getAllRequestByAdminForPeriodWithAlternativeRole(personId, period,
                Long.parseLong(roleId), pageable).forEach(request -> responseList.add(new FullRequestDTO(request)));
        return responseList;
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/admin/count/allRequest/{personId}")
    public ResponseEntity<?> countRequestByPersonIdForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                             @RequestParam(name = "period", defaultValue = "month") String period,
                                                             @PathVariable(name = "personId") Long personId,
                                                             @Pattern(regexp = "(2|3)")
                                                             @RequestParam(name = "role", defaultValue = "2") String roleId)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return new ResponseEntity<>(reportService.countRequestByAdminIdForPeriod(personId, period, Long.parseLong(roleId))
                , HttpStatus.OK);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/count/allRequest/{personId}")
    public ResponseEntity<?> countRequestByPersonIdForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                             @RequestParam(name = "period", defaultValue = "month") String period,
                                                             @PathVariable(name = "personId") Long personId)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return new ResponseEntity<>(reportService.countRequestByPersonIdForPeriod(personId, period), HttpStatus.OK);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/chartsForManager/{personId}")
    public List<ReportDTO> getChartsForManager(@PathVariable(name = "personId") Long personId,
                                               @Pattern(regexp = "(quarter|year|month)")
                                               @RequestParam(name = "period", defaultValue = "month") String period,
                                               @Pattern(regexp = "(pie|area)")
                                               @RequestParam(name = "type", defaultValue = "area") String type)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return reportService.getDataForChartsToManager(personId, period, ChartsType.valueOf(type.toUpperCase()));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/chartsForEmployee/{personId}")
    public List<ReportDTO> getChartsForEmployee(@PathVariable(name = "personId") Long personId,
                                                @Pattern(regexp = "(quarter|year|month)")
                                                @RequestParam(name = "period", defaultValue = "month") String period,
                                                @Pattern(regexp = "(pie|area)")
                                                @RequestParam(name = "type", defaultValue = "area") String type)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return reportService.getDataForChartsToEmployee(personId, period, ChartsType.valueOf(type.toUpperCase()));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/chartsForAdmin/{personId}")
    public List<ReportDTO> getChartsForAdmin(@PathVariable(name = "personId") Long personId,
                                             @Pattern(regexp = "(quarter|year|month)")
                                             @RequestParam(name = "period", defaultValue = "month") String period,
                                             @Pattern(regexp = "(pie|area)")
                                             @RequestParam(name = "type", defaultValue = "area") String type,
                                             @Pattern(regexp = "(2|3)")
                                             @RequestParam(name = "role", defaultValue = "2") String roleId)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return reportService.getDataForChartsToAdmin(personId, period, ChartsType.valueOf(type.toUpperCase()),
                Long.parseLong(roleId));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/calendar")
    public List<CalendarItemDTO> getCalendarDataByPeriod(@RequestParam("start") String start,
                                                         @RequestParam("end") String end,
                                                         Principal principal) throws CurrentUserNotPresentException, ParseException {

        long startTime = Long.parseLong(start);
        long endTime = Long.parseLong(end);
        return calendarService.getDataByPeriod(new Timestamp(startTime*1000), new Timestamp(endTime*1000), principal);
    }
}

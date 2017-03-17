package com.netcracker.controller;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.ChartsType;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@Validated
public class ReportController {


    @Autowired
    private ReportService reportService;

    private static final String JSON_MEDIA_TYPE = "application/json;";

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/allRequest/{personId}")
    public List<FullRequestDTO> getAllRequestByCreatorForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                            @RequestParam(name = "period", defaultValue = "month") String period,
                                                            @PathVariable(name = "personId") Long personId, Pageable pageable) throws CurrentUserNotPresentException, NotDataForThisRoleException {
        List<FullRequestDTO> responseList = new ArrayList<>();
        reportService.getAllRequestByPersonIdForPeriod(personId, period, pageable).forEach(request -> responseList.add(new FullRequestDTO(request)));
        return responseList;
    }

     @GetMapping(produces = JSON_MEDIA_TYPE, value = "/count/allRequest/{personId}")
    public ResponseEntity<?> countRequestByPersonIdForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                               @RequestParam(name = "period", defaultValue = "month") String period,
                                                               @PathVariable(name = "personId") Long personId) throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return new ResponseEntity<>(reportService.countRequestByPersonIdForPeriod(personId,period), HttpStatus.OK);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/chartsForManager/{personId}")
    public List<ReportDTO> getChartsForManager(@PathVariable(name = "personId") Long personId,
                                               @Pattern(regexp = "(quarter|year|month)")
                                                 @RequestParam(name = "period", defaultValue = "month") String period,
                                               @Pattern(regexp = "(pie|area)")
                                                 @RequestParam(name = "type", defaultValue = "area") String type) throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return reportService.getDataForChartsToManager(personId,period, ChartsType.valueOf(type.toUpperCase()));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/chartsForEmployee/{personId}")
    public List<ReportDTO> getChartsForEmployee(@PathVariable(name = "personId") Long personId,
                                               @Pattern(regexp = "(quarter|year|month)")
                                               @RequestParam(name = "period", defaultValue = "month") String period,
                                               @Pattern(regexp = "(pie|area)")
                                               @RequestParam(name = "type", defaultValue = "area") String type) throws CurrentUserNotPresentException, NotDataForThisRoleException {
        return reportService.getDataForChartsToEmployee(personId,period, ChartsType.valueOf(type.toUpperCase()));
    }


}

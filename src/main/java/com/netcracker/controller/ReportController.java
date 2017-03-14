package com.netcracker.controller;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.RequestDTO;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<RequestDTO> getAllRequestByCreatorForPeriod(@Pattern(regexp = "(quarter|year|month)")
                                                            @RequestParam(name = "period", defaultValue = "month") String period,
                                                            @PathVariable(name = "personId") Long personId, Pageable pageable) throws CurrentUserNotPresentException {
        List<RequestDTO> responseList = new ArrayList<>();
        reportService.getAllRequestByPersonIdForPeriod(personId, period, pageable).forEach(request -> responseList.add(new RequestDTO(request)));
        return responseList;
    }

}

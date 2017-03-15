package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotSupportThisRoleExeption;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.netcracker.util.MessageConstant.USER_ERROR_NOT_PRESENT;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private  MessageSource messageSource;

    @Autowired
    private StatusRepository statusRepository;

    @Transactional(readOnly = true)
    public List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period) throws CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person =  personRepository.findOne(personId).orElseThrow(() -> new CurrentUserNotPresentException(
                messageSource.getMessage(USER_ERROR_NOT_PRESENT, new Object[]{personId}, locale)));
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_OFFICE_MANAGER)){
            return requestRepository.findRequestByManagerIdForPeriod(personId, period);
        }
        return requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
    }

    @Transactional(readOnly = true)
    public List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable) throws CurrentUserNotPresentException {
        Person person = getPerson(personId);
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_OFFICE_MANAGER)){
            return requestRepository.findRequestByManagerIdForPeriod(personId, period, pageable);
        }
        return requestRepository.findRequestByEmployeeIdForPeriod(personId, period, pageable);
    }

    @Override
    public List<ReportDTO> getDataForChartsToManager(Long personId, String period, ChartsType chartsType) throws CurrentUserNotPresentException, NotSupportThisRoleExeption {
        Person person = getPerson(personId);
        Role role = getRole(person);
        if(chartsType == ChartsType.AREA){
            List<Request> requestList = requestRepository.findRequestByManagerIdForPeriod(personId, period);
            return buildReportDTOtoAreaCharts(requestList);
        }
        else if(chartsType == ChartsType.PIE){
            List<Request> requestList = requestRepository.findRequestByManagerIdForPeriod(personId, period);
            return buildReportDTOtoPieCharts(requestList);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ReportDTO> getDataForChartsToEmployee(Long personId, String period, ChartsType chartsType) throws CurrentUserNotPresentException, NotSupportThisRoleExeption {
        Person person = getPerson(personId);
        Role role = getRole(person);
        if(chartsType == ChartsType.AREA){
            List<Request> requestList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
            return buildReportDTOtoAreaCharts(requestList);
        }
        else if(chartsType == ChartsType.PIE){
            List<Request> requestList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
            return buildReportDTOtoPieCharts(requestList);
        }
        return new ArrayList<>();
    }

    private List<ReportDTO> buildReportDTOtoAreaCharts(List<Request> requestList){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Collections.sort(requestList, Comparator.comparing(Request::getCreationTime));
        int countRequest = 0;
        LinkedList<ReportDTO> reportDTOList = new LinkedList<>();
        for (Request request : requestList) {
            if (countRequest == 0) {
                reportDTOList.add(new ReportDTO(dateFormat.format(request.getCreationTime()), (long) ++countRequest));
                continue;
            }
            if(dateFormat.format(request.getCreationTime()).equals(reportDTOList.getLast().getLabel())){
                ReportDTO reportDTO = reportDTOList.getLast();
                reportDTO.setValue((long) ++countRequest);
                reportDTOList.set(reportDTOList.size()-1,reportDTO);
            }
            else{
                reportDTOList.addLast(new ReportDTO(dateFormat.format(request.getCreationTime()),(long) ++countRequest));
            }

        }
        return  reportDTOList;
    }

    private List<ReportDTO> buildReportDTOtoPieCharts(List<Request> requestList){
        int freeCount = 0;
        int inProgressCount = 0;
        int closedCount = 0;
        for (Request request: requestList) {
            Status status = statusRepository.findOne(request.getStatus().getId()).get();
            switch (status.getName()){
                case "FREE":
                    freeCount++;
                    break;
                case "IN PROGRESS":
                    inProgressCount++;
                    break;
                case "CLOSED":
                    closedCount++;
                    break;
            }
        }
        LinkedList<ReportDTO> reportDTOList = new LinkedList<>();
        ReportDTO free = new ReportDTO("Free", (long)freeCount);
        ReportDTO inProgress = new ReportDTO("In progress", (long)inProgressCount);
        ReportDTO closed = new ReportDTO("closed", (long)closedCount);
        reportDTOList.add(free);
        reportDTOList.add(inProgress);
        reportDTOList.add(closed);
        return reportDTOList;
    }

    private Person getPerson(Long personId) throws  CurrentUserNotPresentException{
        Locale locale = LocaleContextHolder.getLocale();
        Person person =  personRepository.findOne(personId).orElseThrow(() -> new CurrentUserNotPresentException(
                messageSource.getMessage(USER_ERROR_NOT_PRESENT, new Object[]{personId}, locale)));
        return person;
    }

    private Role getRole(Person person) throws NotSupportThisRoleExeption{
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (!(role.getName().equals(Role.ROLE_OFFICE_MANAGER)||role.getName().equals(Role.ROLE_EMPLOYEE))){
            throw  new NotSupportThisRoleExeption();
        }
        return role;
    }
}
package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.dto.ReportDTO;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.netcracker.util.MessageConstant.NOT_DATA_FOR_THIS_ROLE;
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
    private StatusRepository statusRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RequestService requestService;

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period) throws CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = personRepository.findOne(personId).orElseThrow(() -> new CurrentUserNotPresentException(
                messageSource.getMessage(USER_ERROR_NOT_PRESENT, new Object[]{personId}, locale)));
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_OFFICE_MANAGER)) {
            List<Request> requestArrayList = requestRepository.findAllAssignedRequestToManagerForPeriod(personId, period);
            requestArrayList.forEach(request -> requestService.fill(request));
            return requestArrayList;
        }
        List<Request> requestArrayList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
        requestArrayList.forEach(request -> requestService.fill(request));
        return requestArrayList;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<Request> getAllRequestByPersonIdForPeriod(Long personId, String period, Pageable pageable)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = getPerson(personId);
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_ADMINISTRATOR)) {
            throw new NotDataForThisRoleException(messageSource.getMessage(NOT_DATA_FOR_THIS_ROLE, null, locale));
        }
        if (role.getName().equals(Role.ROLE_OFFICE_MANAGER)) {
            List<Request> requestArrayList = requestRepository.findAllAssignedRequestToManagerForPeriod(personId, period, pageable);
            requestArrayList.forEach(request -> requestService.fill(request));
            return requestArrayList;
        }
        List<Request> requestArrayList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period, pageable);
        requestArrayList.forEach(request -> requestService.fill(request));
        return requestArrayList;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<ReportDTO> getDataForChartsToManager(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        Person person = getPerson(personId);
        Role role = getRole(person);
        if (chartsType == ChartsType.AREA) {
            List<Request> requestList = requestRepository.findAllAssignedRequestToManagerForPeriod(personId, period);
            return period.toLowerCase().equals("year") ? buildReportDTOtoColumnCharts(requestList) : buildReportDTOtoAreaCharts(requestList);
        } else if (chartsType == ChartsType.PIE) {
            List<Request> requestList = requestRepository.findAllAssignedRequestToManagerForPeriod(personId, period);
            return buildReportDTOtoPieCharts(requestList);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<ReportDTO> getDataForChartsToEmployee(Long personId, String period, ChartsType chartsType)
            throws CurrentUserNotPresentException, NotDataForThisRoleException {
        Person person = getPerson(personId);
        Role role = getRole(person);
        if (chartsType == ChartsType.AREA) {
            List<Request> requestList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
            return period.toLowerCase().equals("year") ? buildReportDTOtoColumnCharts(requestList) : buildReportDTOtoAreaCharts(requestList);
        } else if (chartsType == ChartsType.PIE) {
            List<Request> requestList = requestRepository.findRequestByEmployeeIdForPeriod(personId, period);
            return buildReportDTOtoPieCharts(requestList);
        }
        return new ArrayList<>();
    }


    @Override
    public Long countRequestByPersonIdForPeriod(Long personId, String reportPeriod) {
        Person person = personRepository.findOne(personId).get();
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if(role.getName().equals(Role.ROLE_OFFICE_MANAGER)){
            return requestRepository.countAllAssignedRequestToManagerForPeriod(personId, reportPeriod);
        }
        return requestRepository.countRequestByEmployeeIdForPeriod(personId, reportPeriod);
    }

    private List<ReportDTO> buildReportDTOtoAreaCharts(List<Request> requestList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd");
        Collections.sort(requestList, Comparator.comparing(Request::getCreationTime));
        int countRequest = 0;
        LinkedList<ReportDTO> reportDTOList = new LinkedList<>();
        for (Request request : requestList) {
            if (countRequest == 0) {
                reportDTOList.add(new ReportDTO(dateFormat.format(request.getCreationTime()), (long) ++countRequest));
                continue;
            }
            if (dateFormat.format(request.getCreationTime()).equals(reportDTOList.getLast().getLabel())) {
                ReportDTO reportDTO = reportDTOList.getLast();
                reportDTO.setValue((long) ++countRequest);
                reportDTOList.set(reportDTOList.size() - 1, reportDTO);
            } else {
                reportDTOList.addLast(new ReportDTO(dateFormat.format(request.getCreationTime()), (long) ++countRequest));
            }

        }
        return reportDTOList;
    }

    private List<ReportDTO> buildReportDTOtoPieCharts(List<Request> requestList) {
        int freeCount = 0;
        int inProgressCount = 0;
        int closedCount = 0;
        for (Request request : requestList) {
            Status status = statusRepository.findOne(request.getStatus().getId()).get();
            switch (status.getName()) {
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
        ReportDTO free = new ReportDTO("Free", (long) freeCount);
        ReportDTO inProgress = new ReportDTO("In progress", (long) inProgressCount);
        ReportDTO closed = new ReportDTO("Closed", (long) closedCount);
        reportDTOList.add(free);
        reportDTOList.add(inProgress);
        reportDTOList.add(closed);
        return reportDTOList;
    }

    private Person getPerson(Long personId) throws CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = personRepository.findOne(personId).orElseThrow(() -> new CurrentUserNotPresentException(
                messageSource.getMessage(USER_ERROR_NOT_PRESENT, new Object[]{personId}, locale)));
        return person;
    }

    private Role getRole(Person person) throws NotDataForThisRoleException {
        Locale locale = LocaleContextHolder.getLocale();
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_ADMINISTRATOR)) {
            throw new NotDataForThisRoleException(messageSource.getMessage(NOT_DATA_FOR_THIS_ROLE, null, locale));
        }
        return role;
    }

    private List<ReportDTO> buildReportDTOtoColumnCharts(List<Request> requestList) {
        final long INITIAL_COUNT_REQUEST_PER_MONTH = 1L;
        Collections.sort(requestList, Comparator.comparing(Request::getCreationTime));
        Calendar cal = Calendar.getInstance();
        ArrayList<ReportDTO> reportDTOList = new ArrayList<>();
        for (Request request: requestList) {
            int indexOfLast = reportDTOList.size()-1;
            cal.setTime(request.getCreationTime());
            String month = getMonthForInt(cal.get(Calendar.MONTH));
            if(reportDTOList.size()==0){
                reportDTOList.add(new ReportDTO(month,INITIAL_COUNT_REQUEST_PER_MONTH));
                continue;
            }
            if(month.equals(reportDTOList.get(indexOfLast).getLabel())){
               ReportDTO reportDTO = reportDTOList.get(indexOfLast);
               reportDTO.setValue(reportDTO.getValue()+1);
               reportDTOList.set(indexOfLast,reportDTO);
            }
            else {
                reportDTOList.add(new ReportDTO(month,INITIAL_COUNT_REQUEST_PER_MONTH));
            }
        }
        return reportDTOList;
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.UK);
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
}
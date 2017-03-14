package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

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
    @Qualifier("sqlMessageSource")
    private  MessageSource messageSource;

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
        Locale locale = LocaleContextHolder.getLocale();
        Person person =  personRepository.findOne(personId).orElseThrow(() -> new CurrentUserNotPresentException(
                messageSource.getMessage(USER_ERROR_NOT_PRESENT, new Object[]{personId}, locale)));
        Role role = roleRepository.findRoleById(person.getRole().getId()).get();
        if (role.getName().equals(Role.ROLE_OFFICE_MANAGER)){
            return requestRepository.findRequestByManagerIdForPeriod(personId, period, pageable);
        }
        return requestRepository.findRequestByEmployeeIdForPeriod(personId, period, pageable);
    }


}
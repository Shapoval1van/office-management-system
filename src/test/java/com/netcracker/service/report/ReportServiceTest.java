package com.netcracker.service.report;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.NotDataForThisRoleException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.service.request.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ReportServiceTest {
    Optional<Person> personOpt;
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private RequestService requestService;


    @InjectMocks
    private ReportService reportService = new ReportServiceImpl();

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Person person = new Person();
        person.setId((long) 1);
        person.setPassword("test");
        person.setEnabled(true);
        person.setLastName("test");
        person.setFirstName("test");
        Role role = new Role(2);
        role.setName(Role.ROLE_OFFICE_MANAGER);
        person.setRole(role);
        personOpt = Optional.of(person);
    }

    @Test
    public void getAllRequestByPersonIdForPeriod() throws CurrentUserNotPresentException, NotDataForThisRoleException {
        ArrayList<Request>requestArrayList = new ArrayList<>();
        requestArrayList.add(new Request());
        requestArrayList.add(new Request());
        requestArrayList.add(new Request());
        when(personRepository.findOne(anyLong())).thenReturn(personOpt);
        when(roleRepository.findRoleById(personOpt.get().getRole().getId())).thenReturn(Optional.of(personOpt.get().getRole()));
        when(requestRepository.findAllAssignedRequestToManagerForPeriod(eq(personOpt.get().getId()), anyString())).thenReturn(requestArrayList);
        assertEquals(3,reportService.getAllRequestByPersonIdForPeriod(1L, "month").size());
    }

    @Test(expected = CurrentUserNotPresentException.class)
    public void getAllRequestByPersonIdForPeriodWithNotValidId() throws CurrentUserNotPresentException, NotDataForThisRoleException {
        ArrayList<Request>requestArrayList = new ArrayList<>();
        requestArrayList.add(new Request());
        requestArrayList.add(new Request());
        requestArrayList.add(new Request());
        when(personRepository.findOne(130l)).thenReturn(Optional.empty());
        when(messageSource.getMessage(anyObject(),anyObject(),anyObject())).thenReturn("some string");
        reportService.getAllRequestByPersonIdForPeriod(130l, "month");
    }



}

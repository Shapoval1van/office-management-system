package com.netcracker.service.dashboard;


import com.netcracker.model.dto.Dashboard;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.util.enums.role.RoleEnum;
import com.netcracker.util.enums.status.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final PersonRepository personRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public DashboardServiceImpl(PersonRepository personRepository, RequestRepository requestRepository) {
        this.personRepository = personRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Dashboard getData(Principal principal) {
        Person person = personRepository.findPersonByEmail(principal.getName()).get();
        List<Request> requestList = requestRepository.getAllRequestByUser(person.getId());
        List<Request> assignedRequest = requestRepository.getAllAssignedRequestByManager(person.getId());
        List<Integer> employeeData = getEmployeeData(requestList);

        if (RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            return new Dashboard(employeeData.get(0), employeeData.get(1), employeeData.get(2),
                    employeeData.get(3), employeeData.get(4));

        else if (RoleEnum.PROJECT_MANAGER.getId().equals(person.getRole().getId())){
            List<Integer> managerData = getManagerData(assignedRequest);

            return new Dashboard(employeeData.get(0), employeeData.get(1), employeeData.get(2), employeeData.get(3),
                    employeeData.get(4), managerData.get(0), managerData.get(1), managerData.get(2));
        } else {
            List<Person> personList = personRepository.getPersonList();
            List<Request> freeRequestList = requestRepository.getFreeRequests();
            List<Request> allRequestList = requestRepository.getAllRequests();
            List<Integer> managerData = getManagerData(assignedRequest);
            List<Integer> adminData = getAdminData(personList, freeRequestList, allRequestList);

            return new Dashboard(employeeData.get(0), employeeData.get(1), employeeData.get(2), employeeData.get(3),
                    employeeData.get(4), managerData.get(0), managerData.get(1), managerData.get(2),
                    adminData.get(0), adminData.get(1), adminData.get(2), adminData.get(3), adminData.get(4),
                    adminData.get(5), adminData.get(6), adminData.get(7), adminData.get(8), adminData.get(9));
        }
    }

    @Override
    public Dashboard getDataByUser(Long userId) {
        Person person = personRepository.findOne(userId).get();
        List<Request> requestList = requestRepository.getAllRequestByUser(person.getId());
        List<Integer> employeeData = getEmployeeData(requestList);

        if (RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            return new Dashboard(employeeData.get(0), employeeData.get(1), employeeData.get(2),
                    employeeData.get(3), employeeData.get(4));
        else{
            List<Request> assignedRequest = requestRepository.getAllAssignedRequestByManager(person.getId());
            List<Integer> managerData = getManagerData(assignedRequest);

            return new Dashboard(employeeData.get(0), employeeData.get(1), employeeData.get(2), employeeData.get(3),
                    employeeData.get(4), managerData.get(0), managerData.get(1), managerData.get(2));
        }
    }

    private List<Integer> getEmployeeData(List<Request> requestList){
        ArrayList<Integer> employeeData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0));

        employeeData.set(0, requestList.size());
        for (Request request: requestList){
            if (StatusEnum.FREE.getId().equals(request.getStatus().getId()))
                employeeData.set(1, employeeData.get(1)+1);
            else if (StatusEnum.IN_PROGRESS.getId().equals(request.getStatus().getId()))
                employeeData.set(2, employeeData.get(2)+1);
            else if (StatusEnum.CLOSED.getId().equals(request.getStatus().getId()))
                employeeData.set(3, employeeData.get(3)+1);
            else  employeeData.set(4, employeeData.get(4)+1);
        }
        return employeeData;
    }

    private List<Integer> getManagerData(List<Request> assignedRequest){
        ArrayList<Integer> managerData = new ArrayList<>(Arrays.asList(0, 0, 0));

        for (Request assigned : assignedRequest) {
            if (StatusEnum.FREE.getId().equals(assigned.getStatus().getId()))
                managerData.set(0, managerData.get(0)+1);
            else if (StatusEnum.IN_PROGRESS.getId().equals(assigned.getStatus().getId()))
                managerData.set(1, managerData.get(1)+1);
            else if (StatusEnum.CLOSED.getId().equals(assigned.getStatus().getId()))
                managerData.set(2, managerData.get(2)+1);
        }
        return managerData;
    }

    private List<Integer> getAdminData(List<Person> personList, List<Request> freeRequestList, List<Request> allRequestList){
        ArrayList<Integer> adminData = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        adminData.set(0, freeRequestList.size());
        adminData.set(1, allRequestList.size());
        for (Request all : allRequestList) {
            if (StatusEnum.FREE.getId().equals(all.getStatus().getId()))
                adminData.set(2, adminData.get(2)+1);
            else if (StatusEnum.IN_PROGRESS.getId().equals(all.getStatus().getId()))
                adminData.set(3, adminData.get(3)+1);
            else if (StatusEnum.CLOSED.getId().equals(all.getStatus().getId()))
                adminData.set(4, adminData.get(4)+1);
            else adminData.set(5, adminData.get(5)+1);
        }
        adminData.set(6, personList.size());
        for (Person person: personList){
            if (RoleEnum.ADMINISTRATOR.getId().equals(person.getRole().getId()))
                adminData.set(7, adminData.get(7)+1);
            else if (RoleEnum.PROJECT_MANAGER.getId().equals(person.getRole().getId()))
                adminData.set(8, adminData.get(8)+1);
            else adminData.set(9, adminData.get(9)+1);
        }
        return adminData;
    }
}

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
        int freeRequestCount = 0;
        int progressRequestCount = 0;
        int closedRequestCount = 0;
        int canceledRequestCount = 0;

        for (Request r: requestList){
            if (r.getStatus().getId().equals(StatusEnum.FREE.getId()))
                freeRequestCount++;
            else if (r.getStatus().getId().equals(StatusEnum.IN_PROGRESS.getId()))
                progressRequestCount++;
            else if (r.getStatus().getId().equals(StatusEnum.CLOSED.getId()))
                closedRequestCount++;
            else canceledRequestCount++;
        }
        if (person.getRole().getId().equals(RoleEnum.EMPLOYEE.getId()))
            return new Dashboard(requestList.size(), freeRequestCount, progressRequestCount, closedRequestCount, canceledRequestCount);
        else if (person.getRole().getId().equals(RoleEnum.PROJECT_MANAGER.getId())){ // manager data
            List<Request> assignedRequest = requestRepository.getAllAssignedRequest(person.getId());
            int freeAssignedCount = 0;
            int progressAssignedCount = 0;
            int closedAssignedCount = 0;

            for (Request assigned: assignedRequest){
                if (assigned.getStatus().getId().equals(StatusEnum.FREE.getId()))
                    freeAssignedCount++;
                else if (assigned.getStatus().getId().equals(StatusEnum.IN_PROGRESS.getId()))
                    progressAssignedCount++;
                else if (assigned.getStatus().getId().equals(StatusEnum.CLOSED.getId()))
                    closedAssignedCount++;
            }

            return new Dashboard(requestList.size(), freeRequestCount, progressRequestCount, closedRequestCount,
                    canceledRequestCount, freeAssignedCount, progressAssignedCount, closedAssignedCount);
        } else { // administrator data

            List<Request> assignedRequest = requestRepository.getAllAssignedRequest(person.getId());
            int freeAssignedCount = 0;
            int progressAssignedCount = 0;
            int closedAssignedCount = 0;

            for (Request assigned: assignedRequest){
                if (assigned.getStatus().getId().equals(StatusEnum.FREE.getId()))
                    freeAssignedCount++;
                else if (assigned.getStatus().getId().equals(StatusEnum.IN_PROGRESS.getId()))
                    progressAssignedCount++;
                else if (assigned.getStatus().getId().equals(StatusEnum.CLOSED.getId()))
                    closedAssignedCount++;
            }

            List<Person> personList = personRepository.getPersonList();
            List<Request> freeRequestList = requestRepository.getFreeRequests();
            List<Request> allRequestList = requestRepository.getAllRequests();

            int allFreeRequestCount = 0;
            int allProgressRequestCount = 0;
            int allClosedRequestCount = 0;
            int allCanceledRequestCount = 0;

            for (Request all: allRequestList){
                if (all.getStatus().getId().equals(StatusEnum.FREE.getId()))
                    allFreeRequestCount++;
                else if (all.getStatus().getId().equals(StatusEnum.IN_PROGRESS.getId()))
                    allProgressRequestCount++;
                else if (all.getStatus().getId().equals(StatusEnum.CLOSED.getId()))
                    allClosedRequestCount++;
                else allCanceledRequestCount++;
            }

            int administratorCount = 0;
            int managerCount = 0;
            int employeeCount = 0;

            for (Person p: personList){
                if (p.getRole().getId().equals(RoleEnum.ADMINISTRATOR.getId()))
                    administratorCount++;
                else if (p.getRole().getId().equals(RoleEnum.PROJECT_MANAGER.getId()))
                    managerCount++;
                else employeeCount++;
            }

            return new Dashboard(requestList.size(), freeRequestCount, progressRequestCount, closedRequestCount,
                    canceledRequestCount, freeAssignedCount, progressAssignedCount, closedAssignedCount,
                    freeRequestList.size(), allRequestList.size(), allFreeRequestCount, allProgressRequestCount,
                    allClosedRequestCount, allCanceledRequestCount, personList.size(), administratorCount, managerCount,
                    employeeCount);
        }
    }
}

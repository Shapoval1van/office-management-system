package com.netcracker.util;

import com.netcracker.model.entity.*;
import com.netcracker.repository.data.interfaces.*;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ChangeTracker {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private RequestGroupRepository requestGroupRepository;

    public Set<ChangeItem> findMismatching(Request oldRequest, Request newRequest) {
        normalizeRequestObj(oldRequest);
        normalizeRequestObj(newRequest);
        Set<ChangeItem> changeItemSet = new HashSet<>();
        Javers javers = JaversBuilder.javers().build();
        Diff diff = javers.compare(oldRequest, newRequest);
        List<ValueChange> change = diff.getChangesByType(ValueChange.class);
        change.forEach(c -> {
            ChangeItem changeItem = new ChangeItem();
            Optional affectedObject = c.getAffectedObject();
            if (affectedObject.get() instanceof Request) {
                changeItem.setField(fieldRepository.findFieldByName(c.getPropertyName().toUpperCase()).get());
                changeItem.setOldVal(c.getLeft() != null ? c.getLeft().toString() : " ");
                changeItem.setNewVal(c.getRight() != null ? c.getRight().toString() : " ");
                changeItemSet.add(changeItem);
            } else {
                if (affectedObject.get() instanceof Status) {
                    changeItem.setField(fieldRepository.findFieldByName("STATUS").get());
                    changeItem.setOldVal(statusRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
                    changeItem.setNewVal(statusRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
                    changeItemSet.add(changeItem);
                }
                if (affectedObject.get() instanceof Person) {
                    changeItem.setField(fieldRepository.findFieldByName("MANAGER").get());
                    changeItem.setOldVal(c.getLeft()!=null?personRepository.findOne(Long.parseLong(c.getLeft().toString())).get().getFullName():"");
                    changeItem.setNewVal(c.getRight()!=null?personRepository.findOne(Long.parseLong(c.getRight().toString())).get().getFullName():"");
                    changeItemSet.add(changeItem);
                }
                if (affectedObject.get() instanceof Priority) {
                    changeItem.setField(fieldRepository.findFieldByName("PRIORITY").get());
                    changeItem.setOldVal(priorityRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
                    changeItem.setNewVal(priorityRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
                    changeItemSet.add(changeItem);
                }
                if (affectedObject.get() instanceof RequestGroup) {
                    changeItem.setField(fieldRepository.findFieldByName("GROUP").get());
                    changeItem.setOldVal(requestGroupRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
                    changeItem.setNewVal(requestGroupRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
                    changeItemSet.add(changeItem);
                }
            }
        });
        List<ReferenceChange> referenceChanges = diff.getChangesByType(ReferenceChange.class);
        referenceChanges.forEach(r -> {
            String fieldName = r.getPropertyName();
            ChangeItem changeItem = new ChangeItem();
            if ("requestGroup".equals(fieldName)) {
                changeItem.setField(fieldRepository.findFieldByName("GROUP").get());
                changeItem.setOldVal(oldRequest.getRequestGroup()!=null?requestGroupRepository.findOne(oldRequest.getRequestGroup().getId()).get().getName():"");
                changeItem.setNewVal(newRequest.getRequestGroup()!=null?requestGroupRepository.findOne(newRequest.getRequestGroup().getId()).get().getName():"");
                changeItemSet.add(changeItem);
            }
            if ("manager".equals(fieldName)) {
                changeItem.setField(fieldRepository.findFieldByName("MANAGER").get());
                changeItem.setOldVal(oldRequest.getManager()!=null?personRepository.findOne(oldRequest.getManager().getId()).get().getFullName():"");
                changeItem.setNewVal(newRequest.getManager()!=null?personRepository.findOne(newRequest.getManager().getId()).get().getFullName():"");
                changeItemSet.add(changeItem);
            }
        });
        return changeItemSet;
    }

    private void normalizeRequestObj(Request request) {
        if (request.getPriority() != null) {
            request.setPriority(new Priority(request.getPriority().getId()));
        }
        if (request.getStatus() != null) {
            request.setStatus(new Status(request.getStatus().getId()));
        }
        if (request.getManager() != null) {
            request.setManager(new Person(request.getManager().getId()));
        }
        if (request.getRequestGroup() != null) {
            request.setRequestGroup(new RequestGroup(request.getRequestGroup().getId()));
        }
    }
}

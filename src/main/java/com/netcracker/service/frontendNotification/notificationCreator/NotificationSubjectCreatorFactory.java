package com.netcracker.service.frontendNotification.notificationCreator;


import com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl.GroupChangeSubjectCreator;
import com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl.ManagerChangeSubjectCreator;
import com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl.SimpleUpdateSubjectCreator;
import com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl.StatusChangeSubjectCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationSubjectCreatorFactory {
    int STATUS_FIELD_ID = 3;
    int MANAGER_FIELD_ID = 4;
    int GROUP_FIELD_ID = 7;


    private final StatusChangeSubjectCreator statusChangeSubjectCreator;
    private final ManagerChangeSubjectCreator managerChangeSubjectCreator;
    private final GroupChangeSubjectCreator groupChangeSubjectCreator;
    private final SimpleUpdateSubjectCreator simpleUpdateSubjectCreator;

    @Autowired
    public NotificationSubjectCreatorFactory(StatusChangeSubjectCreator statusChangeSubjectCreator,
                                             ManagerChangeSubjectCreator managerChangeSubjectCreator,
                                             GroupChangeSubjectCreator groupChangeSubjectCreator,
                                             SimpleUpdateSubjectCreator simpleUpdateSubjectCreator) {
        this.statusChangeSubjectCreator = statusChangeSubjectCreator;
        this.managerChangeSubjectCreator = managerChangeSubjectCreator;
        this.groupChangeSubjectCreator = groupChangeSubjectCreator;
        this.simpleUpdateSubjectCreator = simpleUpdateSubjectCreator;
    }

    public NotificationSubjectCreator getNotificationSubjectCreator(int changeFieldId){
        if (changeFieldId == STATUS_FIELD_ID) {
            return statusChangeSubjectCreator;
        } else if (changeFieldId == MANAGER_FIELD_ID) {
            return managerChangeSubjectCreator;
        } else if (changeFieldId == GROUP_FIELD_ID) {
            return groupChangeSubjectCreator;
        } else {
            return simpleUpdateSubjectCreator;
        }
    }
}

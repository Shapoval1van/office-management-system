package com.netcracker.model.dto;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

import java.sql.Timestamp;

public class FrontendNotificationDTO {

    private Long id;
    private Person person;
    private String subject = "";
    private Timestamp creationTime;
    private Request request;
    private String notificationTex;

}

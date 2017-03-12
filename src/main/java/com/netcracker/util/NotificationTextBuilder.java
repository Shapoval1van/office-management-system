package com.netcracker.util;


import com.netcracker.model.entity.Notification;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
public class NotificationTextBuilder {

    public String buildText(Notification notification){
        VelocityEngine engine = new VelocityEngine();
        VelocityContext context = new VelocityContext();
        engine.init();
        Template template = engine.getTemplate(notification.getText());
        context.put("name", notification.getPerson().getFirstName());
        if(!notification.getLink().equals("")) {
            context.put("link", notification.getLink());
        }
        StringWriter writer = new StringWriter();
        template.merge(context,writer);
        return writer.toString();
    }
}

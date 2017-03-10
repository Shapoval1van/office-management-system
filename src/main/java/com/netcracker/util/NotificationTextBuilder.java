package com.netcracker.util;


import com.netcracker.model.entity.Notification;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
public class NotificationTextBuilder {

    private VelocityEngine engine;
    private StringWriter writer;
    private Template template;
    private VelocityContext context;

    public NotificationTextBuilder() {
        engine = new VelocityEngine();
        engine.init();
    }

    public synchronized String buildText(Notification notification){
        context = new VelocityContext();
        writer = new StringWriter();
        template = engine.getTemplate(notification.getText());
        context.put("name", notification.getPerson().getFirstName());
        if(!notification.getLink().equals("")) {
            context.put("link", notification.getLink());
        }
        template.merge(context,writer);
        String string = writer.toString();
        return string;
    }
}

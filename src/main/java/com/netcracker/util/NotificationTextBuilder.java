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
        Template template = engine.getTemplate(notification.getTemplate());
        context.put("name", notification.getPerson().getFirstName());
        if (notification.getText() != null){
            context.put("mailBody", notification.getText());
        }
        if (notification.getLink() != null) {
            context.put("link", notification.getLink());
        }
        if (notification.getRequest() != null) {
            context.put("requestId", notification.getRequest().getId());
            context.put("requestStatus", notification.getRequest().getId());
            context.put("requestName", notification.getRequest().getName());
            context.put("requestEstimate", notification.getRequest().getEstimate());
            context.put("requestDescription", notification.getRequest().getDescription());
        }
        if (notification.getChangeItem() != null){
            context.put("changeItem", notification.getChangeItem().getField().getName());
            context.put("oldValue", notification.getChangeItem().getOldVal());
            context.put("newValue", notification.getChangeItem().getNewVal());
        }
        StringWriter writer = new StringWriter();
        template.merge(context,writer);
        return writer.toString();
    }
}

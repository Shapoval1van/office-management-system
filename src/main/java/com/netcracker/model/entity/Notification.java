package com.netcracker.model.entity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;

public class Notification {

    private  VelocityEngine engine = new VelocityEngine();
    private  StringWriter writer = new StringWriter();
    private Template template;
    private VelocityContext context;

    private String doneText;
    private String recipientName;
    private String text;
    private String link;

    private Notification() {
        engine.init();
        recipientName = "";
        text = "";
        link = "";
        context = new VelocityContext();
    }



    public static Builder newNotificationBuilder() {
        return new Notification().new Builder();
    }

    public class Builder {
        private Builder() {

        }

        public Builder setNotificationRecipientName(String name) {
            Notification.this.recipientName = name;
            return this;
        }

        public Builder setNotificationText(String text) {
            Notification.this.text = text;
            return this;
        }


        public Builder setNotificationLink(String link) {
            Notification.this.link = link;
            return this;
        }

        public Notification build() {
            return Notification.this;
        }

    }

    @Override
    public String toString() {
        template = engine.getTemplate(text);
        context.put("name",recipientName);
        if(!link.equals("")) {
            context.put("link", link);
        }
        template.merge(context,writer);
        doneText = writer.toString();
        return doneText;
    }
}

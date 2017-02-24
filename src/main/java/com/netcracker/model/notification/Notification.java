package com.netcracker.model.notification;

public class Notification {
    private String recipientName;
    private String text;
    private String link;

    private Notification() {
        recipientName = "";
        text = "";
        link = "";
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

    public String toString() {
        return recipientName + text + link;
    }
}

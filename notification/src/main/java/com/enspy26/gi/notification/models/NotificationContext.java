package com.enspy26.gi.notification.models;

import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class NotificationContext {
    private NotificationType type;
    private RecipientType recipientType;
    private String fromName;  // nom de l'application
    private String subject;
    private Map<String, Object> variables;
}

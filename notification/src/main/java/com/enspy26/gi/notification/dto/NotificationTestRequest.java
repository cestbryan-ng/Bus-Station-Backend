package com.enspy26.gi.notification.dto;

import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class NotificationTestRequest {
    private NotificationType type;
    private RecipientType recipientType;
    private String email;
    private String recipientName;
    private Map<String, Object> variables;
}
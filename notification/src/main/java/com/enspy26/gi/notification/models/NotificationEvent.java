package com.enspy26.gi.notification.models;

import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationEvent {
    private NotificationType type;
    private RecipientType recipientType;
    private UUID recipientId;
    private Map<String, Object> variables;
}

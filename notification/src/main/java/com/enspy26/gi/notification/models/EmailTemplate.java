package com.enspy26.gi.notification.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailTemplate {
    private String subject;
    private String htmlContent;
    private String textContent;
}
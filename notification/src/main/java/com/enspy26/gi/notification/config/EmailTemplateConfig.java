package com.enspy26.gi.notification.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.email")
@Data
public class EmailTemplateConfig {

    @Value("${spring.mail.username}")
    private String username;

    private String fromEmail;
    private String fromName = "BusStation";
    private String supportEmail;
    private String baseUrl = "https://voyageexpress.com";
    private String logoUrl = "https://voyageexpress.com/logo.png";

    // Couleurs du thème (vous pourrez les adapter selon votre frontend)
    private String primaryColor = "#667eea";
    private String secondaryColor = "#764ba2";
    private String successColor = "#27ae60";
    private String errorColor = "#e74c3c";
    private String warningColor = "#f39c12";

    public String getFromEmail() {
        return fromEmail != null ? fromEmail : username;
    }

    public String getSupportEmail() {
        return supportEmail != null ? supportEmail : username;
    }
}
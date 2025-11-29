package com.enspy26.gi.notification.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class EmailValidationService {

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    public String sanitizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    public boolean isEmailDomainAllowed(String email) {
        if (!isValidEmail(email)) return false;

        String domain = email.substring(email.indexOf("@") + 1);

        // Liste des domaines bloqués (exemple)
        String[] blockedDomains = {"tempmail.com", "10minutemail.com", "guerrillamail.com"};

        for (String blockedDomain : blockedDomains) {
            if (domain.equalsIgnoreCase(blockedDomain)) {
                log.warn("Tentative d'envoi vers un domaine bloqué: {}", domain);
                return false;
            }
        }

        return true;
    }
}
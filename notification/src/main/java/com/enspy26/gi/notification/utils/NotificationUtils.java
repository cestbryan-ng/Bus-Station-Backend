package com.enspy26.gi.notification.utils;

import com.enspy26.gi.database_agence_voyage.enums.RoleType;
import com.enspy26.gi.notification.enums.RecipientType;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@UtilityClass
public class NotificationUtils {

    public static RecipientType getRecipientTypeFromRoles(List<RoleType> roles) {
        if (roles.contains(RoleType.AGENCE_VOYAGE)) {
            return RecipientType.AGENCY;
        } else if (roles.contains(RoleType.ORGANISATION)) {
            return RecipientType.ORGANIZATION;
        } else if (roles.contains(RoleType.EMPLOYE)) {
            return RecipientType.EMPLOYEE;
        } else {
            return RecipientType.USER;
        }
    }

    public static String formatCurrency(double amount) {
        return String.format("%.2f FCFA", amount);
    }

    public static String formatDate(Date date) {
        if (date == null) return "Non spécifiée";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "Non spécifiée";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        return sdf.format(date);
    }

    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
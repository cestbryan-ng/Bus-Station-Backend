package com.enspy26.gi.notification.services;

import com.enspy26.gi.notification.config.EmailTemplateConfig;
import com.enspy26.gi.notification.models.NotificationEvent;
import com.enspy26.gi.notification.models.NotificationContext;
import com.enspy26.gi.notification.templates.EmailTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.Voyage;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final EmailTemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final AgenceVoyageRepository agenceVoyageRepository;
    private final EmailTemplateConfig emailConfig;

    @Async
    public void sendNotification(NotificationEvent event) {
        try {
            log.info("Traitement de la notification: {}", event.getType());

            switch (event.getRecipientType()) {
                case USER -> sendUserNotification(event);
                case AGENCY -> sendAgencyNotification(event);
                case DRIVER -> sendDriverNotification(event);
                case EMPLOYEE -> sendEmployeeNotification(event);
                case ORGANIZATION -> sendOrganizationNotification(event);
                // TODO : Ajouté la notification pour un admin quand les admins seront géres dans l'application
                default -> log.warn("Type de destinataire non supporté: {}", event.getRecipientType());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification: {}", e.getMessage(), e);
        }
    }

    private void sendUserNotification(NotificationEvent event) {
        User user = userRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        NotificationContext context = buildUserContext(user, event);
        sendEmail(user.getEmail(), context);
    }

    private void sendAgencyNotification(NotificationEvent event) {
        AgenceVoyage agence = agenceVoyageRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Agence non trouvée"));

        User chefAgence = userRepository.findById(agence.getUserId())
                .orElseThrow(() -> new RuntimeException("Chef d'agence non trouvé"));

        NotificationContext context = buildAgencyContext(agence, chefAgence, event);
        sendEmail(chefAgence.getEmail(), context);
    }

    private void sendDriverNotification(NotificationEvent event) {
        User driver = userRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Chauffeur non trouvé"));

        NotificationContext context = buildDriverContext(driver, event);
        sendEmail(driver.getEmail(), context);
    }

    private void sendEmployeeNotification(NotificationEvent event) {
        User employee = userRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        NotificationContext context = buildEmployeeContext(employee, event);
        sendEmail(employee.getEmail(), context);
    }

    private void sendOrganizationNotification(NotificationEvent event) {
        // Pour l'organisation, on peut envoyer à tous les chefs d'agence ou à un
        // contact spécifique
        User organizationContact = userRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Contact organisation non trouvé"));

        NotificationContext context = buildOrganizationContext(organizationContact, event);
        sendEmail(organizationContact.getEmail(), context);
    }

    private void sendEmail(String to, NotificationContext context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(emailConfig.getFromEmail());
            helper.setSubject(context.getSubject());
            context.setFromName(emailConfig.getFromName());

            String htmlContent = templateEngine.generateEmailContent(context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MailSendException | MessagingException e) {
            log.error("Erreur lors de l'envoi de l'exmail à {}: {}", to, e.getMessage());
        }
    }

    private NotificationContext buildUserContext(User user, NotificationEvent event) {
        Map<String, Object> variables = new HashMap<>(event.getVariables());
        variables.put("userName", user.getPrenom() + " " + user.getNom());
        variables.put("userEmail", user.getEmail());

        return NotificationContext.builder()
                .type(event.getType())
                .recipientType(event.getRecipientType())
                .subject(getSubjectForType(event.getType(), user.getPrenom()))
                .variables(variables)
                .build();
    }

    private NotificationContext buildAgencyContext(AgenceVoyage agence, User chefAgence, NotificationEvent event) {
        Map<String, Object> variables = new HashMap<>(event.getVariables());
        variables.put("agencyName", agence.getLongName());
        variables.put("agencyShortName", agence.getShortName());
        variables.put("managerName", chefAgence.getPrenom() + " " + chefAgence.getNom());

        return NotificationContext.builder()
                .type(event.getType())
                .recipientType(event.getRecipientType())
                .subject(getSubjectForType(event.getType(), agence.getShortName()))
                .variables(variables)
                .build();
    }

    private NotificationContext buildDriverContext(User driver, NotificationEvent event) {
        Map<String, Object> variables = new HashMap<>(event.getVariables());
        variables.put("driverName", driver.getPrenom() + " " + driver.getNom());

        return NotificationContext.builder()
                .type(event.getType())
                .recipientType(event.getRecipientType())
                .subject(getSubjectForType(event.getType(), driver.getPrenom()))
                .variables(variables)
                .build();
    }

    private NotificationContext buildEmployeeContext(User employee, NotificationEvent event) {
        Map<String, Object> variables = new HashMap<>(event.getVariables());
        variables.put("employeeName", employee.getPrenom() + " " + employee.getNom());

        return NotificationContext.builder()
                .type(event.getType())
                .recipientType(event.getRecipientType())
                .subject(getSubjectForType(event.getType(), employee.getPrenom()))
                .variables(variables)
                .build();
    }

    private NotificationContext buildOrganizationContext(User contact, NotificationEvent event) {
        Map<String, Object> variables = new HashMap<>(event.getVariables());
        variables.put("contactName", contact.getPrenom() + " " + contact.getNom());

        return NotificationContext.builder()
                .type(event.getType())
                .recipientType(event.getRecipientType())
                .subject(getSubjectForType(event.getType(), "Organisation"))
                .variables(variables)
                .build();
    }

    private String getSubjectForType(NotificationType type, String recipientName) {
        return switch (type) {
            case VOYAGE_CREATED -> emailConfig.getFromName() + " - Nouveau voyage créé - " + recipientName;
            case VOYAGE_CANCELLED -> emailConfig.getFromName() + " - Voyage annulé - " + recipientName;
            case RESERVATION_CONFIRMED -> emailConfig.getFromName() + " - Réservation confirmée - " + recipientName;
            case RESERVATION_CANCELLED -> emailConfig.getFromName() + " - Réservation annulée - " + recipientName;
            case PAYMENT_RECEIVED -> emailConfig.getFromName() + " - Paiement reçu - " + recipientName;
            case PAYMENT_FAILED -> emailConfig.getFromName() + " - Échec de paiement - " + recipientName;
            case USER_REGISTERED -> emailConfig.getFromName() + " - Bienvenue sur notre plateforme - " + recipientName;
            case AGENCY_CREATED -> emailConfig.getFromName() + " - Nouvelle agence créée - " + recipientName;
            case DRIVER_ASSIGNED -> emailConfig.getFromName() + " - Nouveau chauffeur assigné - " + recipientName;
            case EMPLOYEE_ADDED -> emailConfig.getFromName() + " - Nouvel employé ajouté - " + recipientName;
            default -> emailConfig.getFromName() + " - Notification - " + recipientName;
        };
    }

    // Méthodes utilitaires pour créer des événements de notification

    public void notifyVoyageCreated(Voyage voyage, UUID agenceId) {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.VOYAGE_CREATED)
                .recipientType(RecipientType.AGENCY)
                .recipientId(agenceId)
                .variables(Map.of(
                        "voyageTitle", voyage.getTitre(),
                        "voyageDate", voyage.getDateDepartPrev(),
                        "voyageDestination", voyage.getLieuArrive()))
                .build();

        sendNotification(event);
    }

    public void notifyReservationConfirmed(Reservation reservation, UUID userId) {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.RESERVATION_CONFIRMED)
                .recipientType(RecipientType.USER)
                .recipientId(userId)
                .variables(Map.of(
                        "reservationId", reservation.getIdReservation(),
                        "passengerCount", reservation.getNbrPassager(),
                        "totalAmount", reservation.getPrixTotal()))
                .build();

        sendNotification(event);
    }

    public void notifyVoyageCancelled(Voyage voyage, List<UUID> affectedUserIds, UUID agenceId) {
        // Notifier l'agence
        NotificationEvent agencyEvent = NotificationEvent.builder()
                .type(NotificationType.VOYAGE_CANCELLED)
                .recipientType(RecipientType.AGENCY)
                .recipientId(agenceId)
                .variables(Map.of(
                        "voyageTitle", voyage.getTitre(),
                        "voyageDate", voyage.getDateDepartPrev(),
                        "affectedUsersCount", affectedUserIds.size()))
                .build();

        sendNotification(agencyEvent);

        // Notifier chaque utilisateur affecté
        affectedUserIds.forEach(userId -> {
            NotificationEvent userEvent = NotificationEvent.builder()
                    .type(NotificationType.VOYAGE_CANCELLED)
                    .recipientType(RecipientType.USER)
                    .recipientId(userId)
                    .variables(Map.of(
                            "voyageTitle", voyage.getTitre(),
                            "voyageDate", voyage.getDateDepartPrev()))
                    .build();

            sendNotification(userEvent);
        });
    }

    public void notifyUserRegistered(User user) {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.USER_REGISTERED)
                .recipientType(RecipientType.USER)
                .recipientId(user.getUserId())
                .variables(Map.of(
                        "welcomeMessage", "Bienvenue sur notre plateforme de voyage!"))
                .build();

        sendNotification(event);
    }

    public void notifyPaymentReceived(Reservation reservation, UUID userId) {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.PAYMENT_RECEIVED)
                .recipientType(RecipientType.USER)
                .recipientId(userId)
                .variables(Map.of(
                        "reservationId", reservation.getIdReservation(),
                        "amount", reservation.getMontantPaye()))
                .build();

        sendNotification(event);
    }

    public void notifyDriverAssigned(UUID driverId, Voyage voyage) {
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationType.DRIVER_ASSIGNED)
                .recipientType(RecipientType.DRIVER)
                .recipientId(driverId)
                .variables(Map.of(
                        "voyageTitle", voyage.getTitre(),
                        "voyageDate", voyage.getDateDepartPrev(),
                        "departure", voyage.getLieuDepart(),
                        "destination", voyage.getLieuArrive()))
                .build();

        sendNotification(event);
    }
}
package com.enspy26.gi.external_api.services;

import com.enspy26.gi.external_api.DTO.request.ContactRequestDTO;
import com.enspy26.gi.external_api.DTO.responses.ContactResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from-email}")
    private String systemEmail;

    @Value("${app.email.from-name}")
    private String systemName;

    @Value("${app.email.contact-recipient:ngoupeyoubryan9@gmail.com}")
    private String contactRecipient;

    /**
     * Envoie un email de contact depuis le formulaire du site
     *
     * @param contactRequest Les données du formulaire de contact
     * @return ContactResponseDTO avec le statut d'envoi
     */
    public ContactResponseDTO sendContactEmail(ContactRequestDTO contactRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Configuration de l'email
            helper.setTo(contactRecipient); // Destinataire hardcodé
            helper.setSubject(contactRequest.getSubject());
            helper.setFrom(systemEmail, systemName);
            helper.setReplyTo(contactRequest.getSenderEmail()); // Pour répondre directement au visiteur

            // Construction du contenu HTML
            String htmlContent = buildEmailContent(contactRequest);
            helper.setText(htmlContent, true);

            // Envoi de l'email
            mailSender.send(message);

            log.info("Email de contact envoyé avec succès de {} ({}) à {}",
                    contactRequest.getSenderName(),
                    contactRequest.getSenderEmail(),
                    contactRecipient);

            return new ContactResponseDTO(
                    true,
                    "Votre message a été envoyé avec succès. Nous vous répondrons dans les plus brefs délais.",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );

        } catch (MessagingException e) {
            log.error("Erreur lors de la création du message email: {}", e.getMessage(), e);
            return new ContactResponseDTO(
                    false,
                    "Erreur lors de la préparation de l'email. Veuillez réessayer.",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        } catch (MailException e) {
            log.error("Erreur lors de l'envoi de l'email: {}", e.getMessage(), e);
            return new ContactResponseDTO(
                    false,
                    "Erreur lors de l'envoi de l'email. Veuillez vérifier votre connexion et réessayer.",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'envoi de l'email de contact: {}", e.getMessage(), e);
            return new ContactResponseDTO(
                    false,
                    "Une erreur inattendue s'est produite. Veuillez réessayer plus tard.",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
        }
    }

    /**
     * Construit le contenu HTML de l'email de contact
     */
    private String buildEmailContent(ContactRequestDTO contactRequest) {
        String phoneDisplay = (contactRequest.getSenderPhone() != null && !contactRequest.getSenderPhone().isEmpty())
                ? contactRequest.getSenderPhone()
                : "Non renseigné";

        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                            line-height: 1.5;
                            color: #171717;
                            background-color: #fafafa;
                            margin: 0;
                            padding: 0;
                            -webkit-font-smoothing: antialiased;
                        }
                        .email-container {
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                        }
                        .email-header {
                            padding: 24px 32px;
                            border-bottom: 1px solid #e5e5e5;
                        }
                        .email-title {
                            font-size: 18px;
                            font-weight: 600;
                            color: #171717;
                            margin: 0;
                        }
                        .email-body {
                            padding: 32px;
                        }
                        .intro-text {
                            font-size: 15px;
                            color: #404040;
                            margin-bottom: 24px;
                        }
                        .contact-field {
                            margin-bottom: 20px;
                            padding-bottom: 20px;
                            border-bottom: 1px solid #f5f5f5;
                        }
                        .contact-field:last-of-type {
                            border-bottom: none;
                        }
                        .field-label {
                            font-size: 13px;
                            color: #737373;
                            margin-bottom: 4px;
                        }
                        .field-value {
                            font-size: 15px;
                            color: #171717;
                            font-weight: 500;
                        }
                        .field-value a {
                            color: #7cab1b;
                            text-decoration: none;
                        }
                        .field-value a:hover {
                            text-decoration: underline;
                        }
                        .message-content {
                            background-color: #fafafa;
                            padding: 16px;
                            margin-top: 8px;
                            font-size: 15px;
                            color: #404040;
                            white-space: pre-wrap;
                            line-height: 1.6;
                        }
                        .action-section {
                            margin-top: 32px;
                            padding-top: 24px;
                            border-top: 1px solid #e5e5e5;
                        }
                        .reply-button {
                            display: inline-block;
                            padding: 10px 20px;
                            background-color: #7cab1b;
                            color: #171717;
                            text-decoration: none;
                            font-weight: 500;
                            font-size: 14px;
                        }
                        .reply-button:hover {
                            background-color: #679419;
                        }
                        .email-footer {
                            padding: 20px 32px;
                            background-color: #fafafa;
                            border-top: 1px solid #e5e5e5;
                        }
                        .footer-text {
                            font-size: 13px;
                            color: #737373;
                            margin: 0;
                        }
                        .footer-timestamp {
                            font-size: 12px;
                            color: #a3a3a3;
                            margin-top: 8px;
                        }
                        @media (max-width: 600px) {
                            .email-container {
                                margin: 0;
                            }
                            .email-header,
                            .email-body,
                            .email-footer {
                                padding: 20px;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="email-header">
                            <h1 class="email-title">Nouveau message de contact</h1>
                        </div>
                
                        <div class="email-body">
                            <p class="intro-text">
                                Un nouveau message a été reçu via le formulaire de contact.
                            </p>
                
                            <div class="contact-field">
                                <div class="field-label">Nom</div>
                                <div class="field-value">%s</div>
                            </div>
                
                            <div class="contact-field">
                                <div class="field-label">Email</div>
                                <div class="field-value">
                                    <a href="mailto:%s">%s</a>
                                </div>
                            </div>
                
                            <div class="contact-field">
                                <div class="field-label">Téléphone</div>
                                <div class="field-value">%s</div>
                            </div>
                
                            <div class="contact-field">
                                <div class="field-label">Message</div>
                                <div class="message-content">%s</div>
                            </div>
                
                            <div class="action-section">
                                <a href="mailto:%s" class="reply-button">Répondre</a>
                            </div>
                        </div>
                
                        <div class="email-footer">
                            <p class="footer-text">
                                Message envoyé depuis BusStation
                            </p>
                            <p class="footer-timestamp">%s</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                contactRequest.getSenderName(),
                contactRequest.getSenderEmail(),
                contactRequest.getSenderEmail(),
                phoneDisplay,
                contactRequest.getMessage(),
                contactRequest.getSenderEmail(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
        );
    }
}
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
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 30px auto;
                            background-color: #ffffff;
                            border-radius: 10px;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: #6149cd;
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 600;
                        }
                        .content {
                            padding: 30px;
                        }
                        .info-block {
                            background-color: #f8f9fa;
                            border-left: 4px solid #667eea;
                            padding: 15px;
                            margin-bottom: 20px;
                            border-radius: 5px;
                        }
                        .info-label {
                            font-weight: 600;
                            color: #667eea;
                            margin-bottom: 5px;
                        }
                        .info-value {
                            color: #555;
                        }
                        .message-box {
                            background-color: #fff;
                            border: 1px solid #e0e0e0;
                            border-radius: 8px;
                            padding: 20px;
                            margin-top: 20px;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #666;
                            border-top: 1px solid #e0e0e0;
                        }
                        .btn-reply {
                            display: inline-block;
                            background-color: #6149CD;
                            color: white;
                            padding: 12px 30px;
                            text-decoration: none;
                            border-radius: 5px;
                            margin-top: 20px;
                            font-weight: 600;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Nouveau message de contact</h1>
                        </div>
                        
                        <div class="content">
                            <p style="font-size: 16px; margin-bottom: 20px;">
                                Vous avez reçu un nouveau message via le formulaire de contact :
                            </p>
                            
                            <div class="info-block">
                                <div class="info-label">Nom complet</div>
                                <div class="info-value">%s</div>
                            </div>
                            
                            <div class="info-block">
                                <div class="info-label">Email</div>
                                <div class="info-value">
                                    <a href="mailto:%s" style="color: #667eea; text-decoration: none;">%s</a>
                                </div>
                            </div>
                            
                            <div class="info-block">
                                <div class="info-label">Téléphone</div>
                                <div class="info-value">%s</div>
                            </div>
                            
                            <div class="message-box">
                                <div class="info-label" style="margin-bottom: 10px;">Message</div>
                                <div style="white-space: pre-wrap; color: #333;">%s</div>
                            </div>
                            
                            <div style="text-align: center;">
                                <a href="mailto:%s" class="btn-reply" style="color: #ffffff;">
                                    Répondre à ce message
                                </a>
                            </div>
                        </div>
                        
                        <div class="footer">
                            <p style="margin: 0;">
                                Cet email a été envoyé depuis le formulaire de contact de <strong>BusStation</strong>
                            </p>
                            <p style="margin: 10px 0 0 0; color: #999;">
                                %s
                            </p>
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
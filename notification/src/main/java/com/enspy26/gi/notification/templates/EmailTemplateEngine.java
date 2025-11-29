package com.enspy26.gi.notification.templates;

import com.enspy26.gi.notification.models.NotificationContext;
import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class EmailTemplateEngine {

    private static final String BASE_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>{{TITLE}}</title>
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                }
                
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    background-color: #f8f9fa;
                }
                
                .email-container {
                    max-width: 600px;
                    margin: 0 auto;
                    background-color: #ffffff;
                    box-shadow: 0 0 20px rgba(0,0,0,0.1);
                }
                
                .header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 30px;
                    text-align: center;
                }
                
                .logo {
                    font-size: 28px;
                    font-weight: bold;
                    margin-bottom: 10px;
                }
                
                .header-subtitle {
                    font-size: 16px;
                    opacity: 0.9;
                }
                
                .content {
                    padding: 40px 30px;
                }
                
                .greeting {
                    font-size: 18px;
                    margin-bottom: 20px;
                    color: #2c3e50;
                }
                
                .message {
                    font-size: 16px;
                    margin-bottom: 30px;
                    line-height: 1.8;
                }
                
                .highlight {
                    background-color: #e8f4fd;
                    border-left: 4px solid #667eea;
                    padding: 20px;
                    margin: 20px 0;
                    border-radius: 0 8px 8px 0;
                }
                
                .button {
                    display: inline-block;
                    padding: 15px 30px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    text-decoration: none;
                    border-radius: 8px;
                    font-weight: bold;
                    margin: 20px 0;
                    transition: transform 0.2s;
                }
                
                .button:hover {
                    transform: translateY(-2px);
                }
                
                .details-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 20px 0;
                    background-color: #f8f9fa;
                    border-radius: 8px;
                    overflow: hidden;
                }
                
                .details-table th,
                .details-table td {
                    padding: 15px;
                    text-align: left;
                    border-bottom: 1px solid #dee2e6;
                }
                
                .details-table th {
                    background-color: #e9ecef;
                    font-weight: bold;
                    color: #495057;
                }
                
                .footer {
                    background-color: #2c3e50;
                    color: white;
                    padding: 30px;
                    text-align: center;
                    font-size: 14px;
                }
                
                .footer-links {
                    margin: 20px 0;
                }
                
                .footer-links a {
                    color: #bdc3c7;
                    text-decoration: none;
                    margin: 0 15px;
                }
                
                .social-links {
                    margin: 20px 0;
                }
                
                .social-links a {
                    display: inline-block;
                    margin: 0 10px;
                    padding: 10px;
                    background-color: #34495e;
                    border-radius: 50%;
                    text-decoration: none;
                    color: white;
                }
                
                @media (max-width: 600px) {
                    .email-container {
                        margin: 0;
                        box-shadow: none;
                    }
                    
                    .content {
                        padding: 20px 15px;
                    }
                    
                    .header {
                        padding: 20px 15px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="header">
                    <div class="logo">{{FROMNAME}}</div>
                    <div class="header-subtitle">Votre partenaire de voyage de confiance</div>
                </div>
                
                <div class="content">
                    {{CONTENT}}
                </div>
                
                <div class="footer">
                    <p><strong>VoyageExpress</strong></p>
                    <p>Votre plateforme de gestion de voyage</p>
                    
                    <div class="footer-links">
                        <a href="#">Nous contacter</a>
                        <a href="#">Support</a>
                        <a href="#">Conditions d'utilisation</a>
                    </div>
                    
                    <div class="social-links">
                        <a href="#">📧</a>
                        <a href="#">📱</a>
                        <a href="#">🌐</a>
                    </div>
                    
                    <p style="margin-top: 20px; font-size: 12px; opacity: 0.8;">
                        © 2025 VoyageExpress. Tous droits réservés.
                    </p>
                </div>
            </div>
        </body>
        </html>
        """;

    public String generateEmailContent(NotificationContext context) {
        String content = generateContentByType(context);
        String title = context.getSubject();

        return BASE_TEMPLATE
                .replace("{{TITLE}}", title)
                .replace("{{CONTENT}}", content)
                .replace("{{FROMNAME}}", context.getFromName());
    }

    private String generateContentByType(NotificationContext context) {
        return switch (context.getType()) {
            case VOYAGE_CREATED -> generateVoyageCreatedContent(context);
            case VOYAGE_CANCELLED -> generateVoyageCancelledContent(context);
            case RESERVATION_CREATED -> generateReservationCreateContent(context);
            case RESERVATION_CONFIRMED -> generateReservationConfirmedContent(context);
            case RESERVATION_CANCELLED -> generateReservationCancelledContent(context);
            case PAYMENT_RECEIVED -> generatePaymentReceivedContent(context);
            case PAYMENT_FAILED -> generatePaymentFailedContent(context);
            case USER_REGISTERED -> generateUserRegisteredContent(context);
            case AGENCY_CREATED -> generateAgencyCreatedContent(context);
            case DRIVER_ASSIGNED -> generateDriverAssignedContent(context);
            case EMPLOYEE_ADDED -> generateEmployeeAddedContent(context);
            default -> generateDefaultContent(context);
        };
    }

    private String generateVoyageCreatedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Un nouveau voyage a été créé avec succès sur votre plateforme.
            </div>
            
            <div class="highlight">
                <h3>🚍 Détails du voyage</h3>
                <table class="details-table">
                    <tr>
                        <th>Titre</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Date de départ</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Destination</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Vous pouvez maintenant gérer ce voyage depuis votre tableau de bord.
            </div>
            
            <a href="#" class="button">Voir le voyage</a>
            """,
                recipientName,
                vars.get("voyageTitle"),
                formatDate((Date) vars.get("voyageDate")),
                vars.get("voyageDestination")
        );
    }

    private String generateVoyageCancelledContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Nous vous informons qu'un voyage a été annulé.
            </div>
            
            <div class="highlight">
                <h3>❌ Voyage annulé</h3>
                <table class="details-table">
                    <tr>
                        <th>Titre</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Date prévue</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Si vous aviez une réservation pour ce voyage, vous recevrez un email séparé 
                concernant le remboursement.
            </div>
            
            <a href="#" class="button">Voir mes réservations</a>
            """,
                recipientName,
                vars.get("voyageTitle"),
                formatDate((Date) vars.get("voyageDate"))
        );
    }

    private String generateReservationCreateContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);
        String answer = switch (context.getRecipientType()) {
            case USER -> "Votre";
            case AGENCY -> "Une nouvelle";
            default -> "Votre";
        };

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Excellente nouvelle ! %s réservation a été crée avec succès.
            </div>
            
            <div class="highlight">
                <h3>✅ Réservation confirmée</h3>
                <table class="details-table">
                    <tr>
                        <th>Numéro de réservation</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Nombre de passagers</th>
                        <td>%d</td>
                    </tr>
                    <tr>
                        <th>Montant Payée</th>
                        <td>%.2f FCFA</td>
                    </tr>
                    <tr>
                        <th>Montant total</th>
                        <td>%.2f FCFA</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Votre voyage est maintenant confirmé. Vous recevrez d'autres informations 
                concernant les détails du départ prochainement.
            </div>
            
            <a href="#" class="button">Voir ma réservation</a>
            """,
                recipientName,
                answer,
                vars.get("reservationId"),
                vars.get("passengerCount"),
                vars.get("totalPayed"),
                vars.get("totalAmount")
        );
    }

    private String generateReservationConfirmedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Excellente nouvelle ! Votre réservation a été confirmée avec succès.
            </div>
            
            <div class="highlight">
                <h3>✅ Réservation confirmée</h3>
                <table class="details-table">
                    <tr>
                        <th>Numéro de réservation</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Nombre de passagers</th>
                        <td>%d</td>
                    </tr>
                    <tr>
                        <th>Montant total</th>
                        <td>%.2f FCFA</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Votre voyage est maintenant confirmé. Vous recevrez d'autres informations 
                concernant les détails du départ prochainement.
            </div>
            
            <a href="#" class="button">Voir ma réservation</a>
            """,
                recipientName,
                vars.get("reservationId"),
                vars.get("passengerCount"),
                vars.get("totalAmount")
        );
    }

    private String generateReservationCancelledContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Nous vous confirmons l'annulation de votre réservation.
            </div>
            
            <div class="highlight">
                <h3>❌ Réservation annulée</h3>
                <table class="details-table">
                    <tr>
                        <th>Numéro de réservation</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Le remboursement sera traité selon notre politique d'annulation. 
                Vous recevrez un coupon de remboursement si applicable.
            </div>
            
            <a href="#" class="button">Voir mes coupons</a>
            """,
                recipientName,
                vars.get("reservationId")
        );
    }

    private String generatePaymentReceivedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Nous avons bien reçu votre paiement. Merci pour votre confiance !
            </div>
            
            <div class="highlight">
                <h3>💰 Paiement reçu</h3>
                <table class="details-table">
                    <tr>
                        <th>Réservation</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Montant</th>
                        <td>%.2f FCFA</td>
                    </tr>
                    <tr>
                        <th>Date</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Votre réservation sera confirmée automatiquement une fois le paiement validé.
            </div>
            
            <a href="#" class="button">Voir ma réservation</a>
            """,
                recipientName,
                vars.get("reservationId"),
                vars.get("amount"),
                formatDate(new Date())
        );
    }

    private String generatePaymentFailedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Nous vous informons qu'un problème est survenu lors du traitement de votre paiement.
            </div>
            
            <div class="highlight" style="border-left-color: #e74c3c; background-color: #fdf2f2;">
                <h3>❌ Échec du paiement</h3>
                <table class="details-table">
                    <tr>
                        <th>Réservation</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Montant</th>
                        <td>%.2f FCFA</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Veuillez réessayer le paiement ou contacter notre support pour assistance.
            </div>
            
            <a href="#" class="button">Réessayer le paiement</a>
            """,
                recipientName,
                vars.get("reservationId"),
                vars.get("amount")
        );
    }

    private String generateUserRegisteredContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bienvenue %s ! 🎉</div>
            
            <div class="message">
                Nous sommes ravis de vous accueillir sur VoyageExpress, votre nouvelle plateforme 
                de réservation de voyage.
            </div>
            
            <div class="highlight">
                <h3>🚀 Commencez votre aventure</h3>
                <p>Avec VoyageExpress, vous pouvez :</p>
                <ul style="margin: 15px 0; padding-left: 20px;">
                    <li>Réserver des voyages en quelques clics</li>
                    <li>Suivre vos réservations en temps réel</li>
                    <li>Gérer vos paiements en toute sécurité</li>
                    <li>Accéder à un support client 24/7</li>
                </ul>
            </div>
            
            <div class="message">
                %s
            </div>
            
            <a href="#" class="button">Découvrir les voyages</a>
            """,
                recipientName,
                vars.get("welcomeMessage")
        );
    }

    private String generateAgencyCreatedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Félicitations %s ! 🎊</div>
            
            <div class="message">
                Votre agence de voyage a été créée avec succès sur notre plateforme.
            </div>
            
            <div class="highlight">
                <h3>🏢 Votre agence est prête</h3>
                <table class="details-table">
                    <tr>
                        <th>Nom de l'agence</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Nom court</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Vous pouvez maintenant créer vos premiers voyages et commencer à accueillir des clients.
            </div>
            
            <a href="#" class="button">Accéder au tableau de bord</a>
            """,
                recipientName,
                vars.get("agencyName"),
                vars.get("agencyShortName")
        );
    }

    private String generateDriverAssignedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Vous avez été assigné(e) comme chauffeur pour un nouveau voyage.
            </div>
            
            <div class="highlight">
                <h3>🚗 Nouveau voyage assigné</h3>
                <table class="details-table">
                    <tr>
                        <th>Voyage</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Date de départ</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Départ</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Destination</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Veuillez vous préparer pour ce voyage et consulter tous les détails dans votre espace chauffeur.
            </div>
            
            <a href="#" class="button">Voir les détails du voyage</a>
            """,
                recipientName,
                vars.get("voyageTitle"),
                formatDate((Date) vars.get("voyageDate")),
                vars.get("departure"),
                vars.get("destination")
        );
    }

    private String generateEmployeeAddedContent(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bienvenue dans l'équipe %s ! 👥</div>
            
            <div class="message">
                Vous avez été ajouté(e) comme employé(e) dans notre organisation.
            </div>
            
            <div class="highlight">
                <h3>🎯 Votre nouveau rôle</h3>
                <table class="details-table">
                    <tr>
                        <th>Poste</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Département</th>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <th>Agence</th>
                        <td>%s</td>
                    </tr>
                </table>
            </div>
            
            <div class="message">
                Vous allez recevoir vos accès et informations de connexion prochainement.
            </div>
            
            <a href="#" class="button">Accéder à mon espace</a>
            """,
                recipientName,
                vars.getOrDefault("poste", "Non spécifié"),
                vars.getOrDefault("departement", "Non spécifié"),
                vars.getOrDefault("agencyName", "Non spécifiée")
        );
    }

    private String generateDefaultContent(NotificationContext context) {
        String recipientName = getRecipientName(context);

        return String.format("""
            <div class="greeting">Bonjour %s,</div>
            
            <div class="message">
                Vous avez une nouvelle notification de %s.
            </div>
            
            <div class="highlight">
                <h3>📢 Notification</h3>
                <p>Type: %s</p>
            </div>
            
            <div class="message">
                Pour plus de détails, veuillez consulter votre tableau de bord.
            </div>
            
            <a href="#" class="button">Voir le tableau de bord</a>
            """,
                recipientName,
                context.getFromName(),
                context.getType()
        );
    }

    private String getRecipientName(NotificationContext context) {
        Map<String, Object> vars = context.getVariables();

        return switch (context.getRecipientType()) {
            case USER -> (String) vars.getOrDefault("userName", "Cher(e) client(e)");
            case AGENCY -> (String) vars.getOrDefault("managerName", "Cher(e) partenaire");
            case DRIVER -> (String) vars.getOrDefault("driverName", "Cher(e) chauffeur");
            case EMPLOYEE -> (String) vars.getOrDefault("employeeName", "Cher(e) collaborateur/trice");
            case ORGANIZATION -> (String) vars.getOrDefault("contactName", "Cher(e) responsable");
            default -> "Cher(e) utilisateur/trice";
        };
    }

    private String formatDate(Date date) {
        if (date == null) return "Non spécifiée";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        return sdf.format(date);
    }
}
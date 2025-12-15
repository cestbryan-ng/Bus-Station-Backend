package com.enspy26.gi.notification.controllers;

import com.enspy26.gi.notification.services.NotificationService;
import com.enspy26.gi.notification.factory.NotificationFactory;
import com.enspy26.gi.notification.models.NotificationEvent;
import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Contrôleur pour tester le système de notifications
 * À utiliser uniquement en développement
 */
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "API de test pour le système de notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Endpoint pour tester l'envoi d'une notification
     */
    @PostMapping("/test")
    @Operation(
            summary = "Tester l'envoi d'une notification",
            description = "Envoie une notification de test à l'adresse email spécifiée. Endpoint de développement uniquement."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification envoyée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Notification de test envoyée à: test@example.com")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur lors de l'envoi de la notification",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Erreur: Message d'erreur détaillé")
                    )
            )
    })
    public Mono<ResponseEntity<String>> testNotification(
            @Parameter(description = "Adresse email du destinataire", required = true, example = "test@example.com")
            @RequestParam String email) {
        return Mono.fromRunnable(() -> {
            // Créer une notification de test
            NotificationEvent testEvent = NotificationEvent.builder()
                    .type(NotificationType.SYSTEM_UPDATE)
                    .recipientType(RecipientType.USER)
                    .recipientId(UUID.fromString("b0d94cb1-ce06-455b-9947-e2099424fb4e")) // ID fictif
                    .variables(Map.of(
                            "userName", "Utilisateur Test",
                            "userEmail", email,
                            "testMessage", "Ceci est un email de test du système de notification VoyageExpress"))
                    .build();
            notificationService.sendNotification(testEvent);
        })
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.ok("Notification de test envoyée à: " + email)))
                .onErrorResume(Exception.class, e -> {
                    log.error("Erreur lors de l'envoi de la notification de test: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body("Erreur: " + e.getMessage()));
                });
    }

    /**
     * Endpoint pour tester une notification d'inscription
     */
    @PostMapping("/test-welcome/{userId}")
    @Operation(
            summary = "Tester une notification de bienvenue",
            description = "Envoie une notification de bienvenue à un utilisateur existant basée sur son ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification de bienvenue envoyée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Notification de bienvenue envoyée à: user@example.com")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur lors de l'envoi ou utilisateur non trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Erreur: Utilisateur non trouvé")
                    )
            )
    })
    public Mono<ResponseEntity<String>> testWelcomeNotification(
            @Parameter(description = "Identifiant unique de l'utilisateur", required = true)
            @PathVariable UUID userId) {
        return Mono.fromCallable(() -> userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé")))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> Mono.fromRunnable(() -> notificationService.sendNotification(
                        NotificationFactory.createUserRegisteredEvent(user)))
                        .subscribeOn(Schedulers.boundedElastic())
                        .then(Mono.just(ResponseEntity.ok("Notification de bienvenue envoyée à: " + user.getEmail()))))
                .onErrorResume(Exception.class, e -> {
                    log.error("Erreur lors de l'envoi de la notification de bienvenue: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body("Erreur: " + e.getMessage()));
                });
    }

    /**
     * Endpoint pour tester différents types de notifications
     */
    @PostMapping("/test-type")
    @Operation(
            summary = "Tester une notification par type",
            description = "Envoie une notification selon le type spécifié avec des données de test appropriées"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification envoyée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Notification de type VOYAGE_CREATED envoyée à: test@example.com")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Erreur lors de l'envoi de la notification",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Erreur: Message d'erreur détaillé")
                    )
            )
    })
    public Mono<ResponseEntity<String>> testNotificationByType(
            @Parameter(description = "Type de notification à envoyer", required = true)
            @RequestParam NotificationType type,
            @Parameter(description = "Adresse email du destinataire", required = true, example = "test@example.com")
            @RequestParam String email,
            @Parameter(description = "Nom du destinataire (optionnel)", required = false, example = "Jean Dupont")
            @RequestParam(required = false) String recipientName) {

        return Mono.fromRunnable(() -> {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", recipientName != null ? recipientName : "Utilisateur Test");
            variables.put("userEmail", email);

            // Ajouter des variables spécifiques selon le type
            switch (type) {
                case VOYAGE_CREATED -> {
                    variables.put("voyageTitle", "Voyage Test Yaoundé-Douala");
                    variables.put("voyageDate", new java.util.Date());
                    variables.put("voyageDestination", "Douala");
                }
                case RESERVATION_CONFIRMED -> {
                    variables.put("reservationId", UUID.randomUUID().toString());
                    variables.put("passengerCount", 2);
                    variables.put("totalAmount", 15000.0);
                }
                case PAYMENT_RECEIVED -> {
                    variables.put("reservationId", UUID.randomUUID().toString());
                    variables.put("amount", 15000.0);
                }
                // Ajoutez d'autres cas selon vos besoins
            }

            NotificationEvent event = NotificationEvent.builder()
                    .type(type)
                    .recipientType(RecipientType.USER)
                    .recipientId(UUID.randomUUID())
                    .variables(variables)
                    .build();

            notificationService.sendNotification(event);
        })
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.just(ResponseEntity.ok("Notification de type " + type + " envoyée à: " + email)))
                .onErrorResume(Exception.class, e -> {
                    log.error("Erreur lors de l'envoi de la notification: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body("Erreur: " + e.getMessage()));
                });
    }

    /**
     * Endpoint pour lister les types de notifications disponibles
     */
    @GetMapping("/types")
    @Operation(
            summary = "Lister les types de notifications",
            description = "Retourne la liste de tous les types de notifications disponibles dans le système"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des types de notifications récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = NotificationType.class,
                                    description = "Tableau des types de notifications disponibles"
                            )
                    )
            )
    })
    public Mono<ResponseEntity<NotificationType[]>> getNotificationTypes() {
        return Mono.fromCallable(NotificationType::values)
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }
}
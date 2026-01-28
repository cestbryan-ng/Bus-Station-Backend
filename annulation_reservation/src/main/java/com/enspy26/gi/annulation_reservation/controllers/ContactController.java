package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.external_api.DTO.request.ContactRequestDTO;
import com.enspy26.gi.external_api.DTO.responses.ContactResponseDTO;
import com.enspy26.gi.external_api.services.ContactEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contact", description = "API pour gérer les messages de contact du site SafaraPlace")
public class ContactController {

    private final ContactEmailService contactEmailService;

    @Operation(
            summary = "Envoyer un message de contact",
            description = "Permet aux visiteurs du site d'envoyer un message via le formulaire de contact. " +
                    "L'email sera envoyé automatiquement à l'administrateur du site."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email envoyé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContactResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données du formulaire invalides",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors de l'envoi de l'email",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/send")
    public ResponseEntity<ContactResponseDTO> sendContactMessage(
            @Valid @RequestBody ContactRequestDTO contactRequest
    ) {
        log.info("📨 Réception d'un message de contact de: {} ({})",
                contactRequest.getSenderName(),
                contactRequest.getSenderEmail());

        ContactResponseDTO response = contactEmailService.sendContactEmail(contactRequest);

        if (response.isSuccess()) {
            log.info("✅ Email de contact envoyé avec succès");
            return ResponseEntity.ok(response);
        } else {
            log.error("❌ Échec de l'envoi de l'email de contact");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(
            summary = "Health check du service email",
            description = "Vérifie que le service d'envoi d'emails est opérationnel"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Service opérationnel",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\": \"UP\", \"service\": \"Contact Email Service\"}");
    }
}
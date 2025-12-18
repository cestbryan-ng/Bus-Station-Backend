package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.dto.organization.UpdateOrganizationRequest;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.dto.organization.OrganizationDetailsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.enspy26.gi.annulation_reservation.services.OrganizationService;
import com.enspy26.gi.database_agence_voyage.dto.CreateOrganizationRequest;
import com.enspy26.gi.database_agence_voyage.dto.OrganizationDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for organization management
 * Handles HTTP requests related to organizations
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(
            summary = "Obtenir toutes les agences d'une organisations",
            description = "Récupère la liste de toutes les agences de l'organisation"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AgenceVoyage.class))
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/agencies/{organisationId}")
    public ResponseEntity<List<AgenceVoyage>> getAllCoupons(@PathVariable UUID organisationId) {
        List<AgenceVoyage> agencies = organizationService.findAllAgencies(organisationId);
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }

    @Operation(
            summary = "Obtenir les détails d'une organisation",
            description = "Récupère toutes les informations détaillées d'une organisation par son ID. " +
                    "Accessible par le DG de l'organisation, les BSM et les administrateurs."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organisation trouvée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organisation non trouvée",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - L'utilisateur n'a pas les permissions nécessaires",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{organisationId}")
    public ResponseEntity<OrganizationDetailsDTO> getOrganizationById(@PathVariable UUID organisationId) {
        OrganizationDetailsDTO organization = organizationService.getOrganizationById(organisationId);
        return ResponseEntity.ok(organization);
    }

    @Operation(
            summary = "Créer une nouvelle organisation (Société de Transport)",
            description = "Permet à un Chef de Société de créer le profil de sa société de transport avec toutes les informations légales requises. " +
                    "Cette étape est obligatoire avant de pouvoir demander une implantation dans les gares routières."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Organisation créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides - Vérifier les champs obligatoires (nom, email, numéro de contribuable, matricule, etc.)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit - Une organisation avec ce numéro de contribuable ou matricule existe déjà",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - L'utilisateur n'a pas le rôle 'CHEF_SOCIETE'",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur interne",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDto created_organization = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created_organization);
    }

    @Operation(
            summary = "Récupérer la liste de toutes les organisations",
            description = "Permet de récupérer la liste de toutes les sociétés de transport enregistrées dans le système. " +
                    "Cette route peut être utilisée par les BSM pour consulter les organisations ou pour des besoins administratifs."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrganizationDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - L'utilisateur n'a pas les permissions nécessaires",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur serveur interne",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        List<OrganizationDto> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    @Operation(
            summary = "Modifier une organisation existante",
            description = "Permet au DG de mettre à jour les informations de son organisation. " +
                    "Seuls les champs fournis dans la requête seront mis à jour (mise à jour partielle)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organisation modifiée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides - Vérifier le format des champs",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organisation non trouvée",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - Seul le DG de l'organisation peut la modifier",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{organisationId}")
    public ResponseEntity<OrganizationDetailsDTO> updateOrganization(
            @PathVariable UUID organisationId,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        OrganizationDetailsDTO updated_organization = organizationService.updateOrganization(organisationId, request);
        return ResponseEntity.ok(updated_organization);
    }

    @Operation(
            summary = "Supprimer une organisation",
            description = "Permet à un administrateur de supprimer une organisation du système. " +
                    "La suppression est logique (soft delete) et l'organisation ne peut être supprimée " +
                    "que si elle n'a plus d'agences actives."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Organisation supprimée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organisation non trouvée",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflit - L'organisation a des agences actives",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - Seul un administrateur peut supprimer une organisation",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{organisationId}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID organisationId) {
        organizationService.deleteOrganization(organisationId);
        return ResponseEntity.noContent().build();
    }
}
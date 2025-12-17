package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
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

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Obtenir toutes les agences d'une organisations", description = "Récupère la liste de toutes les agences de l'organisation")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgenceVoyage.class))))})
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/agencies/{organisationId}")
    public ResponseEntity<List<AgenceVoyage>> getAllCoupons(@PathVariable UUID organisationId) {
        List<AgenceVoyage> agencies = organizationService.findAllAgencies(organisationId);
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }

    @Operation(summary = "Créer une nouvelle organisation (Société de Transport)", description = "Permet à un Chef de Société de créer le profil de sa société de transport avec toutes les informations légales requises. " + "Cette étape est obligatoire avant de pouvoir demander une implantation dans les gares routières.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Organisation créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDto.class))), @ApiResponse(responseCode = "400", description = "Données invalides - Vérifier les champs obligatoires (nom, email, numéro de contribuable, matricule, etc.)", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "409", description = "Conflit - Une organisation avec ce numéro de contribuable ou matricule existe déjà", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "401", description = "Non authentifié - Token JWT manquant ou invalide", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "403", description = "Interdit - L'utilisateur n'a pas le rôle 'CHEF_SOCIETE'", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Erreur serveur interne", content = @Content(mediaType = "application/json"))})
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDto createdOrganization = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
    }

    @Operation(summary = "Récupérer la liste de toutes les organisations", description = "Permet de récupérer la liste de toutes les sociétés de transport enregistrées dans le système. " + "Cette route peut être utilisée par les BSM pour consulter les organisations ou pour des besoins administratifs.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = OrganizationDto.class)))), @ApiResponse(responseCode = "401", description = "Non authentifié - Token JWT manquant ou invalide", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "403", description = "Interdit - L'utilisateur n'a pas les permissions nécessaires", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "500", description = "Erreur serveur interne", content = @Content(mediaType = "application/json"))})
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<List<OrganizationDto>> getAllOrganizations() {
        List<OrganizationDto> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }
}

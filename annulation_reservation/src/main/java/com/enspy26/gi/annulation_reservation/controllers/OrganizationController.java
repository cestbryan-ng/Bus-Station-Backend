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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgenceVoyage.class))))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/agencies/{organisationId}")
    public ResponseEntity<List<AgenceVoyage>> getAllCoupons(@PathVariable UUID organisationId) {
        List<AgenceVoyage> agencies = organizationService.findAllAgencies(organisationId);
        return new ResponseEntity<>(agencies, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDto createdOrganization = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrganization);
    }
}

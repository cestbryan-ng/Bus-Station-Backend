package com.enspy26.gi.annulation_reservation.controllers;

import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.dto.agence.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.annulation_reservation.services.AgenceVoyageService;
import com.enspy26.gi.annulation_reservation.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/agence")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AgenceVoyageController {

  private final UserService userService;
  private final AgenceVoyageService agenceVoyageService;

  @Operation(summary = "Create a new travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Travel agency created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or duplicate agency names"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PostMapping
  public Mono<ResponseEntity<AgenceVoyage>> createAgence(@Valid @RequestBody AgenceVoyageDTO agenceDTO) {
    return Mono.fromCallable(() -> {
      AgenceVoyage agence = userService.createAgenceVoyage(agenceDTO);
      return new ResponseEntity<>(agence, HttpStatus.CREATED);
    }).subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Update an existing travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Travel agency updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or duplicate agency names"),
      @ApiResponse(responseCode = "404", description = "Agency not found")
  })
  @PatchMapping("/{id}")
  public Mono<ResponseEntity<AgenceVoyage>> updateAgence(@PathVariable("id") UUID agencyId,
      @RequestBody AgenceVoyageDTO agenceDTO) {
    return Mono.fromCallable(() -> {
      AgenceVoyage agence = userService.updateAgenceVoyage(agencyId, agenceDTO);
      return new ResponseEntity<>(agence, HttpStatus.OK);
    }).subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Delete a travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Travel agency deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Agency not found")
  })
  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteAgence(@PathVariable("id") UUID agencyId) {
    return Mono.fromRunnable(() -> userService.deleteAgenceVoyage(agencyId))
        .subscribeOn(Schedulers.boundedElastic())
        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Retouner une agence d evoyage à partir de l'id du chef d'agence")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Agence trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "404", description = "Agence non trouvée")
  })
  @GetMapping("/chef-agence/{id}")
  public ResponseEntity<AgenceVoyage> getChefAgenceById(@PathVariable UUID id) {
    AgenceVoyage agence = this.agenceVoyageService.findAgenceByChefAgenceId(id);
    if (agence == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(agence, HttpStatus.OK);
  }

    @Operation(
            summary = "Obtenir toutes les agences",
            description = "Récupère la liste de toutes les agences avec filtres optionnels. " +
                    "Supporte la pagination et le tri. Utilisé par les BSM pour gérer les agences, " +
                    "et par les DG pour voir leurs agences."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres de requête invalides",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<AgencePreviewDTO>> getAllAgencies(
            @RequestParam(required = false) String ville,
            @RequestParam(name = "organisation_id", required = false) UUID organisationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sort_by", defaultValue = "longName") String sortBy,
            @RequestParam(name = "sort_order", defaultValue = "asc") String sortOrder
    ) {
        Page<AgencePreviewDTO> agences = agenceVoyageService.getAllAgencies(
                ville,
                organisationId,
                page,
                size,
                sortBy,
                sortOrder
        );
        return ResponseEntity.ok(agences);
    }

    @Operation(
            summary = "Obtenir les détails d'une agence",
            description = "Récupère toutes les informations détaillées d'une agence par son ID. " +
                    "Accessible par tous les utilisateurs authentifiés. Utilisé pour consulter " +
                    "le profil d'une agence avant réservation ou validation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Agence trouvée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgenceDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agence non trouvée",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<AgenceDetailsDTO> getAgencyById(@PathVariable UUID id) {
        AgenceDetailsDTO agence = agenceVoyageService.getAgencyById(id);
        return ResponseEntity.ok(agence);
    }

    @Operation(
            summary = "Obtenir les agences en attente de validation",
            description = "Récupère la liste des agences avec le statut EN_ATTENTE. " +
                    "Utilisé par les BSM pour voir les demandes d'implantation à traiter. " +
                    "Peut être filtré par ville pour que chaque BSM ne voie que sa zone."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - Accessible uniquement aux BSM",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/pending-validation")
    public ResponseEntity<Page<AgencePreviewDTO>> getPendingValidationAgencies(
            @RequestParam(required = false) String ville,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<AgencePreviewDTO> agences = agenceVoyageService.getPendingValidationAgencies(ville, page, size);
        return ResponseEntity.ok(agences);
    }

    @Operation(
            summary = "Valider une agence",
            description = "Permet à un BSM de valider une agence en attente d'approbation. " +
                    "L'agence passe du statut EN_ATTENTE à VALIDEE et peut commencer à opérer. " +
                    "Cette action est irréversible et doit être effectuée après vérification complète."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Agence validée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgenceDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Agence ne peut pas être validée (statut incorrect)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agence ou BSM non trouvé",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - Seuls les BSM peuvent valider des agences",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/validate")
    public ResponseEntity<AgenceDetailsDTO> validateAgency(
            @PathVariable UUID id,
            @Valid @RequestBody ValidateAgenceRequest request
    ) {
        AgenceDetailsDTO validated_agence = agenceVoyageService.validateAgency(id, request);
        return ResponseEntity.ok(validated_agence);
    }

    @Operation(
            summary = "Rejeter une agence",
            description = "Permet à un BSM de rejeter une agence en attente d'approbation. " +
                    "L'agence passe du statut EN_ATTENTE à REJETEE. Un motif de rejet détaillé " +
                    "est obligatoire pour informer le DG des raisons du refus. " +
                    "Le DG pourra corriger les problèmes et soumettre une nouvelle demande."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Agence rejetée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgenceDetailsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Agence ne peut pas être rejetée (statut incorrect) ou motif invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agence ou BSM non trouvé",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Interdit - Seuls les BSM peuvent rejeter des agences",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/reject")
    public ResponseEntity<AgenceDetailsDTO> rejectAgency(
            @PathVariable UUID id,
            @Valid @RequestBody RejectAgenceRequest request
    ) {
        AgenceDetailsDTO rejected_agence = agenceVoyageService.rejectAgency(id, request);
        return ResponseEntity.ok(rejected_agence);
    }

    @Operation(
            summary = "Obtenir les agences par ville",
            description = "Récupère toutes les agences d'une ville spécifique. " +
                    "Peut être filtré par statut de validation. Utilisé par les BSM pour gérer " +
                    "leur zone géographique et par les clients pour rechercher des agences dans une ville."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres de requête invalides",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/by-city/{ville}")
    public ResponseEntity<Page<AgencePreviewDTO>> getAgenciesByCity(
            @PathVariable String ville,
            @RequestParam(name = "statut_validation", required = false) StatutValidation statutValidation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sort_by", defaultValue = "longName") String sortBy,
            @RequestParam(name = "sort_order", defaultValue = "asc") String sortOrder
    ) {
        Page<AgencePreviewDTO> agences = agenceVoyageService.getAgenciesByCity(
                ville,
                statutValidation,
                page,
                size,
                sortBy,
                sortOrder
        );
        return ResponseEntity.ok(agences);
    }

    @Operation(
            summary = "Obtenir les agences validées",
            description = "Récupère uniquement les agences avec le statut VALIDEE. " +
                    "Ces agences sont approuvées par les BSM et peuvent opérer. " +
                    "Utilisé principalement par les clients pour rechercher des agences actives " +
                    "et par les DG pour voir leurs agences en exploitation. " +
                    "Peut être filtré par ville et/ou organisation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres de requête invalides",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/validated")
    public ResponseEntity<Page<AgencePreviewDTO>> getValidatedAgencies(
            @RequestParam(required = false) String ville,
            @RequestParam(name = "organisation_id", required = false) UUID organisationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sort_by", defaultValue = "longName") String sortBy,
            @RequestParam(name = "sort_order", defaultValue = "asc") String sortOrder
    ) {
        Page<AgencePreviewDTO> agences = agenceVoyageService.getValidatedAgencies(
                ville,
                organisationId,
                page,
                size,
                sortBy,
                sortOrder
        );
        return ResponseEntity.ok(agences);
    }
}

package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.dto.statistics.*;
import com.enspy26.gi.annulation_reservation.services.StatisticService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/statistics")
@SecurityRequirement(name = "bearerAuth") // JWT requis pour tout le contrôleur
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @Operation(summary = "Obtenir les statistiques générales d'une agence",
            description = "Récupère toutes les statistiques chiffrées d'une agence : nombre d'employés, voyages, réservations, revenus, etc.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AgenceStatisticsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/agence/{agenceId}/general")
    public ResponseEntity<?> getAgenceStatistics(@PathVariable UUID agenceId) {
        try {
            log.info("Récupération des statistiques générales pour l'agence {}", agenceId);
            AgenceStatisticsDTO statistics = statisticService.getAgenceStatistics(agenceId);
            return ResponseEntity.ok(statistics);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération des statistiques pour l'agence {}: {}",
                    agenceId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération des statistiques pour l'agence {}: {}",
                    agenceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques");
        }
    }

    @Operation(summary = "Obtenir les évolutions dans le temps pour une agence",
            description = "Récupère les données d'évolution temporelle : réservations, voyages, revenus et utilisateurs sur les derniers mois.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Évolutions récupérées avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AgenceEvolutionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/agence/{agenceId}/evolution")
    public ResponseEntity<?> getAgenceEvolution(@PathVariable UUID agenceId) {
        try {
            log.info("Récupération des évolutions temporelles pour l'agence {}", agenceId);
            AgenceEvolutionDTO evolution = statisticService.getAgenceEvolution(agenceId);
            return ResponseEntity.ok(evolution);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération des évolutions pour l'agence {}: {}",
                    agenceId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération des évolutions pour l'agence {}: {}",
                    agenceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des évolutions");
        }
    }

    @Operation(summary = "Obtenir toutes les statistiques d'une agence",
            description = "Récupère à la fois les statistiques générales et les évolutions temporelles en un seul appel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Toutes les statistiques récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/agence/{agenceId}/complete")
    public ResponseEntity<?> getCompleteAgenceStatistics(@PathVariable UUID agenceId) {
        try {
            log.info("Récupération des statistiques complètes pour l'agence {}", agenceId);

            AgenceStatisticsDTO statistics = statisticService.getAgenceStatistics(agenceId);
            AgenceEvolutionDTO evolutions = statisticService.getAgenceEvolution(agenceId);

            // Créer un objet combiné
            var completeStats = new Object() {
                public final AgenceStatisticsDTO general = statistics;
                public final AgenceEvolutionDTO evolution = evolutions;
            };

            return ResponseEntity.ok(completeStats);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération des statistiques complètes pour l'agence {}: {}",
                    agenceId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération des statistiques complètes pour l'agence {}: {}",
                    agenceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques complètes");
        }
    }

    @Operation(
            summary = "Obtenir les statistiques générales d'une organisation",
            description = "Récupère les statistiques consolidées de toutes les agences d'une organisation. " +
                    "Inclut le nombre d'agences, d'employés, de chauffeurs, de voyages, les revenus totaux, " +
                    "et le taux d'occupation moyen. Utilisé par le DG pour avoir une vue d'ensemble de son organisation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationStatisticsDTO.class)
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
                    description = "Interdit - Seul le DG de l'organisation peut voir ces statistiques",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/organisation/{orgId}/general")
    public ResponseEntity<?> getOrganizationStatistics(@PathVariable UUID orgId) {
        try {
            log.info("Récupération des statistiques générales pour l'organisation {}", orgId);
            OrganizationStatisticsDTO statistics = statisticService.getOrganizationStatistics(orgId);
            return ResponseEntity.ok(statistics);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération des statistiques pour l'organisation {}: {}",
                    orgId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération des statistiques pour l'organisation {}: {}",
                    orgId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques");
        }
    }

    @Operation(
            summary = "Comparer les performances des agences d'une organisation",
            description = "Récupère les statistiques détaillées de chaque agence d'une organisation pour permettre " +
                    "la comparaison. Inclut pour chaque agence : nombre d'employés, chauffeurs, véhicules, " +
                    "voyages, réservations, revenus et taux d'occupation. Identifie également la meilleure " +
                    "agence performante basée sur les revenus."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques de comparaison récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrganizationAgenciesComparisonDTO.class)
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
                    description = "Interdit - Seul le DG de l'organisation peut voir ces statistiques",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/organisation/{orgId}/agencies")
    public ResponseEntity<?> getOrganizationAgenciesComparison(@PathVariable UUID orgId) {
        try {
            log.info("Récupération de la comparaison des agences pour l'organisation {}", orgId);
            OrganizationAgenciesComparisonDTO comparison = statisticService.getOrganizationAgenciesComparison(orgId);
            return ResponseEntity.ok(comparison);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération de la comparaison pour l'organisation {}: {}",
                    orgId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération de la comparaison pour l'organisation {}: {}",
                    orgId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de la comparaison");
        }
    }

    @Operation(
            summary = "Obtenir la vue d'ensemble BSM pour une ville",
            description = "Récupère les statistiques agrégées de toutes les agences d'une ville. " +
                    "Utilisé par le BSM pour avoir une vue d'ensemble de sa zone de responsabilité. " +
                    "Inclut le nombre d'agences par statut, les demandes en attente, le nombre total " +
                    "de voyages, réservations, véhicules et chauffeurs dans la ville."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BsmOverviewDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aucune agence trouvée dans cette ville",
                    content = @Content(mediaType = "application/json")
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
    @GetMapping("/bsm/{ville}/overview")
    public ResponseEntity<?> getBsmOverview(@PathVariable String ville) {
        try {
            log.info("Récupération de la vue d'ensemble BSM pour la ville {}", ville);
            BsmOverviewDTO overview = statisticService.getBsmOverview(ville);
            return ResponseEntity.ok(overview);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération de la vue d'ensemble pour la ville {}: {}",
                    ville, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération de la vue d'ensemble pour la ville {}: {}",
                    ville, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de la vue d'ensemble");
        }
    }

    @Operation(
            summary = "Obtenir les statistiques d'un voyage",
            description = "Récupère toutes les statistiques et informations d'un voyage spécifique. " +
                    "Inclut les infos générales (titre, trajet, chauffeur, véhicule), les stats de réservation " +
                    "(places, revenus, taux d'occupation), et les données pour graphiques (répartition par statut, " +
                    "genre, âge, évolution dans le temps)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VoyageStatisticsDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Voyage non trouvé",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié - Token JWT manquant ou invalide",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/voyage/{voyageId}")
    public ResponseEntity<?> getVoyageStatistics(@PathVariable UUID voyageId) {
        try {
            log.info("Récupération des statistiques pour le voyage {}", voyageId);
            VoyageStatisticsDTO statistics = statisticService.getVoyageStatistics(voyageId);
            return ResponseEntity.ok(statistics);
        } catch (ResponseStatusException e) {
            log.error("Erreur lors de la récupération des statistiques pour le voyage {}: {}",
                    voyageId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            log.error("Erreur interne lors de la récupération des statistiques pour le voyage {}: {}",
                    voyageId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des statistiques");
        }
    }
}
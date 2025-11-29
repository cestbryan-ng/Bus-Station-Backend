package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceStatisticsDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceEvolutionDTO;
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
}
package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyagePreviewDTO;
import com.enspy26.gi.database_agence_voyage.models.SoldeIndemnisation;
import com.enspy26.gi.annulation_reservation.services.SoldeIndemnisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/solde-indemnisation")
public class SoldeIndeminsationController {

    private SoldeIndemnisationService soldeIndemnisationService;

    public SoldeIndeminsationController(SoldeIndemnisationService soldeIndemnisationService) {
        this.soldeIndemnisationService = soldeIndemnisationService;
    }

    @Operation(summary = "Obtenir tous les sodes d'indemnisation", description = "Récupère la liste de tous les sodes d'indemnisation enregistrés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoyagePreviewDTO.class))))
    })
    @GetMapping
    public Mono<ResponseEntity<List<SoldeIndemnisation>>> getAllVoyages() {
        return Mono.fromCallable(() -> soldeIndemnisationService.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .map(soldes -> new ResponseEntity<>(soldes, HttpStatus.OK));
    }

    @Operation(summary = "Obtenir un solde indemnisation par son ID", description = "Récupère un solde indemnisation en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "solde indemnisation trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoldeIndemnisation.class))),
            @ApiResponse(responseCode = "404", description = "solde indemnisation non trouvé")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SoldeIndemnisation>> getVoyageById(@PathVariable UUID id) {
        return Mono.fromCallable(() -> soldeIndemnisationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(solde -> {
                    if (solde == null) {
                        return new ResponseEntity<SoldeIndemnisation>(HttpStatus.NOT_FOUND);
                    }
                    return new ResponseEntity<>(solde, HttpStatus.OK);
                });
    }

    @Operation(summary = "Créer un solde indemnisation", description = "Ajoute un nouveau solde indemnisation à la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "solde indemnisation créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoldeIndemnisation.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public Mono<ResponseEntity<SoldeIndemnisation>> createVoyage(
            @RequestBody SoldeIndemnisation solde) {
        return Mono.fromCallable(() -> soldeIndemnisationService.create(solde))
                .subscribeOn(Schedulers.boundedElastic())
                .map(createdSolde -> new ResponseEntity<>(createdSolde, HttpStatus.CREATED));
    }

    @Operation(summary = "Mettre à jour un solde indemnisation", description = "Modifie un solde indemnisation existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "solde indemnisation mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SoldeIndemnisation.class))),
            @ApiResponse(responseCode = "404", description = "solde indemnisation non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SoldeIndemnisation>> updateVoyage(@PathVariable UUID id,
            @RequestBody SoldeIndemnisation solde) {
        return Mono.fromCallable(() -> soldeIndemnisationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingSolde -> {
                    if (existingSolde == null) {
                        return Mono.just(new ResponseEntity<SoldeIndemnisation>(HttpStatus.NOT_FOUND));
                    }
                    solde.setIdSolde(id); // Assurez-vous que l'ID est correctement défini
                    return Mono.fromCallable(() -> soldeIndemnisationService.update(solde))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(updatedSolde -> new ResponseEntity<>(updatedSolde, HttpStatus.OK));
                });
    }

    @Operation(summary = "Supprimer un solde indemnisation", description = "Supprime un solde indemnisation en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "solde indemnisation supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "solde indemnisation non trouvé")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteVoyage(@PathVariable UUID id) {
        return Mono.fromCallable(() -> soldeIndemnisationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingSolde -> {
                    if (existingSolde == null) {
                        return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
                    }
                    return Mono.fromRunnable(() -> soldeIndemnisationService.delete(id))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                });
    }
}

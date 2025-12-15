package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.models.PolitiqueAnnulation;
import com.enspy26.gi.annulation_reservation.services.PolitiqueAnnulationService;
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
@RequestMapping("/politique-annulation")
public class PolitiqueAnnulationController {

    private final PolitiqueAnnulationService politiqueAnnulationService;

    public PolitiqueAnnulationController(PolitiqueAnnulationService politiqueAnnulationService) {
        this.politiqueAnnulationService = politiqueAnnulationService;
    }

    @Operation(summary = "Obtenir toutes les politiques d'annulation", description = "Récupère la liste de toutes les politiques d'annulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PolitiqueAnnulation.class))))
    })
    @GetMapping
    public Mono<ResponseEntity<List<PolitiqueAnnulation>>> getAllPolicies() {
        return Mono.fromCallable(() -> politiqueAnnulationService.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .map(policies -> new ResponseEntity<>(policies, HttpStatus.OK));
    }

    @Operation(summary = "Obtenir une politique d'annulation par ID", description = "Récupère une politique d'annulation spécifique par ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Politique trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PolitiqueAnnulation.class))),
            @ApiResponse(responseCode = "404", description = "Politique non trouvée")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PolitiqueAnnulation>> getPolicyById(@PathVariable UUID id) {
        return Mono.fromCallable(() -> politiqueAnnulationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(policy -> {
                    if (policy == null) {
                        return new ResponseEntity<PolitiqueAnnulation>(HttpStatus.NOT_FOUND);
                    }
                    return new ResponseEntity<>(policy, HttpStatus.OK);
                });
    }

    @Operation(summary = "Créer une politique d'annulation", description = "Ajoute une nouvelle politique d'annulation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Politique créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PolitiqueAnnulation.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public Mono<ResponseEntity<PolitiqueAnnulation>> createPolicy(
            @RequestBody PolitiqueAnnulation policy) {
        return Mono.fromCallable(() -> politiqueAnnulationService.create(policy))
                .subscribeOn(Schedulers.boundedElastic())
                .map(createdPolicy -> new ResponseEntity<>(createdPolicy, HttpStatus.CREATED));
    }

    @Operation(summary = "Mettre à jour une politique d'annulation", description = "Modifie une politique d'annulation existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Politique mise à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PolitiqueAnnulation.class))),
            @ApiResponse(responseCode = "404", description = "Politique non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PolitiqueAnnulation>> updatePolicy(@PathVariable UUID id,
                                                                  @RequestBody PolitiqueAnnulation policy) {
        return Mono.fromCallable(() -> politiqueAnnulationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingPolicy -> {
                    if (existingPolicy == null) {
                        return Mono.just(new ResponseEntity<PolitiqueAnnulation>(HttpStatus.NOT_FOUND));
                    }
                    policy.setIdPolitique(id);
                    return Mono.fromCallable(() -> politiqueAnnulationService.update(policy))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(updatedPolicy -> new ResponseEntity<>(updatedPolicy, HttpStatus.OK));
                });
    }

    @Operation(summary = "Supprimer une politique d'annulation", description = "Supprime une politique d'annulation par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Politique supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Politique non trouvée")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePolicy(@PathVariable UUID id) {
        return Mono.fromCallable(() -> politiqueAnnulationService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingPolicy -> {
                    if (existingPolicy == null) {
                        return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
                    }
                    return Mono.fromRunnable(() -> politiqueAnnulationService.delete(id))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                });
    }
}

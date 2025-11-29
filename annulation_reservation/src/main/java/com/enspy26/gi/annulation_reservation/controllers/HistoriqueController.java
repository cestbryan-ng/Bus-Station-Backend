package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.models.Historique;
import com.enspy26.gi.annulation_reservation.services.HistoriqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/historique")
public class HistoriqueController {

        private final HistoriqueService historiqueService;

        @Operation(summary = "Obtenir tous les historique", description = "Récupère la liste de tous les historiques.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Historique.class))))
        })
        @GetMapping
        public Mono<ResponseEntity<List<Historique>>> getAllHistoriques() {
                return Mono.fromCallable(() -> historiqueService.findAll())
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(historiques -> new ResponseEntity<>(historiques, HttpStatus.OK));
        }

        @Operation(summary = "Obtenir tous les historique de reservation d'un utilisateur", description = "Récupère la liste de tous les historiques de reservations d'un utilisateur.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Historique.class))))
        })
        @GetMapping("reservation/{idUtilisateur}")
        public Mono<ResponseEntity<List<Historique>>> getHistoriqueReservation(@PathVariable UUID idUtilisateur) {
                return Mono.fromCallable(() -> historiqueService.historiqueReservationParUtilisateur(idUtilisateur))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(historiques -> new ResponseEntity<>(historiques, HttpStatus.OK));
        }

        @Operation(summary = "Obtenir un historique par ID", description = "Récupère un historique spécifique par ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Historique trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Historique.class))),
                        @ApiResponse(responseCode = "404", description = "Historique non trouvé")
        })
        @GetMapping("/{id}")
        public Mono<ResponseEntity<Historique>> getHistoriqueById(@PathVariable UUID id) {
                return Mono.fromCallable(() -> historiqueService.findById(id))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(historique -> {
                                        if (historique == null) {
                                                return new ResponseEntity<Historique>(HttpStatus.NOT_FOUND);
                                        }
                                        return new ResponseEntity<>(historique, HttpStatus.OK);
                                });
        }

        @Operation(summary = "Créer un historique", description = "Ajoute un nouvel historique.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Historique créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Historique.class))),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PostMapping
        public Mono<ResponseEntity<Historique>> createHistorique(@RequestBody Historique historique) {
                return Mono.fromCallable(() -> historiqueService.create(historique))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(createdHistorique -> new ResponseEntity<>(createdHistorique, HttpStatus.CREATED));
        }

        @Operation(summary = "Mettre à jour un historique", description = "Modifie un historique existant.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Historique mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Historique.class))),
                        @ApiResponse(responseCode = "404", description = "Historique non trouvé"),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PutMapping("/{id}")
        public Mono<ResponseEntity<Historique>> updateHistorique(@PathVariable UUID id,
                        @RequestBody Historique historique) {
                return Mono.fromCallable(() -> historiqueService.findById(id))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(existingHistorique -> {
                                        if (existingHistorique == null) {
                                                return Mono.just(new ResponseEntity<Historique>(HttpStatus.NOT_FOUND));
                                        }
                                        historique.setIdHistorique(id);
                                        return Mono.fromCallable(() -> historiqueService.update(historique))
                                                                .subscribeOn(Schedulers.boundedElastic())
                                                                .map(updatedHistorique -> new ResponseEntity<>(updatedHistorique, HttpStatus.OK));
                                });
        }

        @Operation(summary = "Supprimer un historique", description = "Supprime un historique par son ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Historique supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Historique non trouvé")
        })
        @DeleteMapping("/{id}")
        public Mono<ResponseEntity<Void>> deleteHistorique(@PathVariable UUID id) {
                return Mono.fromCallable(() -> historiqueService.findById(id))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(existingHistorique -> {
                                        if (existingHistorique == null) {
                                                return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
                                        }
                                        return Mono.fromRunnable(() -> historiqueService.delete(id))
                                                                .subscribeOn(Schedulers.boundedElastic())
                                                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                                });
        }
}

package com.enspy26.gi.plannification_voyage.controllers;

import com.enspy26.gi.database_agence_voyage.models.Baggage;
import com.enspy26.gi.plannification_voyage.services.BaggageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/baggages")
@RequiredArgsConstructor
public class BaggageController {

        private final BaggageService baggageService;

        @Operation(summary = "Obtenir tous les bagages", description = "Récupère la liste de tous les bagages.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Baggage.class))))
        })
        @GetMapping
        public ResponseEntity<List<Baggage>> getAllBaggages() {
                List<Baggage> baggages = baggageService.findAll();
                return new ResponseEntity<>(baggages, HttpStatus.OK);
        }

        @Operation(summary = "Obtenir un bagage par ID", description = "Récupère un bagage spécifique par son ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Bagage trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Baggage.class))),
                        @ApiResponse(responseCode = "404", description = "Bagage non trouvé")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Baggage> getBaggageById(@PathVariable UUID id) {
                Baggage baggage = baggageService.findById(id);
                if (baggage == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(baggage, HttpStatus.OK);
        }

        @Operation(summary = "Créer un bagage", description = "Ajoute un nouveau bagage.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Bagage créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Baggage.class))),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PostMapping
        public ResponseEntity<Baggage> createBaggage(@RequestBody Baggage baggage) {
                Baggage createdBaggage = baggageService.create(baggage);
                return new ResponseEntity<>(createdBaggage, HttpStatus.CREATED);
        }

        @Operation(summary = "Mettre à jour un bagage", description = "Modifie un bagage existant.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Bagage mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Baggage.class))),
                        @ApiResponse(responseCode = "404", description = "Bagage non trouvé"),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Baggage> updateBaggage(@PathVariable UUID id, @RequestBody Baggage baggage) {
                if (baggageService.findById(id) == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                baggage.setIdBaggage(id);
                Baggage updatedBaggage = baggageService.update(baggage);
                return new ResponseEntity<>(updatedBaggage, HttpStatus.OK);
        }

        @Operation(summary = "Supprimer un bagage", description = "Supprime un bagage par son ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Bagage supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Bagage non trouvé")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteBaggage(@PathVariable UUID id) {
                if (baggageService.findById(id) == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                baggageService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}

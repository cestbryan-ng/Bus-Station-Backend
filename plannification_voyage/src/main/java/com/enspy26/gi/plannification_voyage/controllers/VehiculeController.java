package com.enspy26.gi.plannification_voyage.controllers;

import com.enspy26.gi.database_agence_voyage.dto.vehicule.VehiculeDTO;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;
import com.enspy26.gi.plannification_voyage.services.VehiculeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicule")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VehiculeController {

        private final VehiculeService vehiculeService;

        @Operation(summary = "Obtenir tous les vehicules", description = "Récupère la liste de tous les vehicules enregistrés.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Vehicule.class))))
        })
        @GetMapping
        public ResponseEntity<Page<Vehicule>> getAllVehicules(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<Vehicule> vehicules = vehiculeService.findAll(page, size);
                return new ResponseEntity<>(vehicules, HttpStatus.OK);
        }

        @Operation(summary = "Obtenir tous les vehicules d'une agence", description = "Récupère la liste de tous les vehicules de l'agence dont l'id est passé dans la route enregistrés.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Vehicule.class))))
        })
        @GetMapping("/agence/{idAgence}")
        public ResponseEntity<List<Vehicule>> getAllVehiculesForAgence(@PathVariable UUID idAgence) {
                List<Vehicule> vehicules = vehiculeService.findByIdAgenceVoyage(idAgence);
                return new ResponseEntity<>(vehicules, HttpStatus.OK);
        }

        @Operation(summary = "Obtenir un vehicule par ID", description = "Récupère un vehicule en fonction de son identifiant.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Vehicule trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Vehicule.class))),
                        @ApiResponse(responseCode = "404", description = "Vehicule non trouvé")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Vehicule> getVehiculeById(@PathVariable UUID id) {
                Vehicule vehicule = vehiculeService.findById(id);
                if (vehicule == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(vehicule, HttpStatus.OK);
        }

        @Operation(summary = "Créer un vehicule", description = "Ajoute un nouveau vehicule à la base de données.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Vehicule créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Vehicule.class))),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PostMapping
        public ResponseEntity<Vehicule> createVehicule(@RequestBody VehiculeDTO vehicule) {
                Vehicule createdVehicule = vehiculeService.create(vehicule);
                // return ResponseEntity.status(HttpStatus.CREATED).body(createdVoyage);
                return new ResponseEntity<>(createdVehicule, HttpStatus.CREATED);
        }

        @Operation(summary = "Mettre à jour un vehicule", description = "Modifie un vehicule existant.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Vehicule mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Vehicule.class))),
                        @ApiResponse(responseCode = "404", description = "vehicule non trouvé"),
                        @ApiResponse(responseCode = "400", description = "Données invalides")
        })
        @PutMapping("/{id}")
        public ResponseEntity<Vehicule> updateVehicule(@PathVariable UUID id, @RequestBody VehiculeDTO vehicule) {
                if (vehiculeService.findById(id) == null) {
                        // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                Vehicule updatedVehicule = vehiculeService.update(id, vehicule);
                return new ResponseEntity<>(updatedVehicule, HttpStatus.OK);
        }

        @Operation(summary = "Supprimer un vehicule", description = "Supprime un vehicule en fonction de son identifiant.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Vehicule supprimé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Vehicule non trouvé")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteVehicule(@PathVariable UUID id) {
                if (vehiculeService.findById(id) == null) {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                vehiculeService.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}

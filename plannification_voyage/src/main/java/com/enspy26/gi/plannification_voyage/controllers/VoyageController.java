package com.enspy26.gi.plannification_voyage.controllers;

import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageCreateRequestDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageDetailsDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyagePreviewDTO;
import com.enspy26.gi.database_agence_voyage.models.Voyage;
import com.enspy26.gi.plannification_voyage.services.VoyageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/voyage")
public class VoyageController {

    private final VoyageService voyageService;

    public VoyageController(VoyageService voyageService) {
        this.voyageService = voyageService;
    }

    @Operation(summary = "Obtenir tous les voyages", description = "Récupère la liste de tous les voyages (champs stricts pour le preview) enregistrés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoyagePreviewDTO.class))))
    })
    @GetMapping("/all")
    public ResponseEntity<Page<VoyagePreviewDTO>> getAllVoyages(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<VoyagePreviewDTO> voyages = voyageService.findAllPreview(page, size);
        return new ResponseEntity<>(voyages, HttpStatus.OK);
    }

    @Operation(summary = "Obtenir les détails d'un voyage par ID", description = "Récupère un voyage en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voyage trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoyageDetailsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    })
    @GetMapping("byId/{id}")
    public ResponseEntity<VoyageDetailsDTO> getVoyageById(@PathVariable UUID id) {
        VoyageDetailsDTO voyage = voyageService.findById(id);
        if (voyage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(voyage, HttpStatus.OK);
    }

    @Operation(summary = "Créer un voyage", description = "Ajoute un nouveau voyage à la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voyage créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Voyage.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping("/create")
    public ResponseEntity<VoyageDetailsDTO> createVoyage(@RequestBody @Valid VoyageCreateRequestDTO voyage) {
        VoyageDetailsDTO createdVoyage = voyageService.create(voyage);
        // return ResponseEntity.status(HttpStatus.CREATED).body(createdVoyage);
        return new ResponseEntity<>(createdVoyage, HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour un voyage", description = "Modifie un voyage existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voyage mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Voyage.class))),
            @ApiResponse(responseCode = "404", description = "Voyage non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Voyage> updateVoyage(@PathVariable UUID id, @RequestBody Voyage voyage) {
        if (voyageService.findById(id) == null) {
            // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Voyage updatedVoyage = voyageService.update(voyage);
        return new ResponseEntity<>(updatedVoyage, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer un voyage", description = "Supprime un voyage en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Voyage supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Voyage non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoyage(@PathVariable UUID id) {
        if (voyageService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        voyageService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Obtenir tous les voyages d'une agence", description = "Récupère la liste de tous les voyages d'une agence spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoyagePreviewDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Agence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/agence/{agenceId}")
    public ResponseEntity<?> getAllVoyagesByAgence(
            @PathVariable UUID agenceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            Page<VoyageDetailsDTO> voyages = voyageService.findAllByAgenceId(agenceId, page, size);
            return ResponseEntity.ok(voyages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des voyages de l'agence");
        }
    }

    @Operation(
            summary = "Rechercher des voyages",
            description = "Recherche des voyages par ville de départ/arrivée (obligatoire) " +
                    "et optionnellement par zone et date. " +
                    "- ville_depart/ville_arrive : Ville (ex: 'Yaoundé', 'Douala') " +
                    "- zone_depart/zone_arrive : Zone dans la ville (ex: 'Mvan', 'Akwa') " +
                    "- date_depart : Date au format 'yyyy-MM-dd'"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des voyages correspondant aux critères",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VoyagePreviewDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Paramètres de recherche invalides"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchVoyages(
            @RequestParam(name = "ville_depart") String villeDepart,
            @RequestParam(name = "ville_arrive") String villeArrive,
            @RequestParam(name = "zone_depart", required = false) String zoneDepart,
            @RequestParam(name = "zone_arrive", required = false) String zoneArrive,
            @RequestParam(name = "date_depart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDepart,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        try {
            Page<VoyagePreviewDTO> voyages = voyageService.searchVoyages(
                    villeDepart,
                    villeArrive,
                    zoneDepart,
                    zoneArrive,
                    dateDepart,
                    page,
                    size
            );
            return ResponseEntity.ok(voyages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la recherche des voyages: " + e.getMessage());
        }
    }
}

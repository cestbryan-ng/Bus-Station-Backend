package com.enspy26.gi.plannification_voyage.controllers;

import com.enspy26.gi.database_agence_voyage.dto.classVoyage.ClassVoyageDTO;
import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;
import com.enspy26.gi.plannification_voyage.services.ClassVoyageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/class-voyage")
public class ClassVoyageController {

    private final ClassVoyageService classVoyageService;

    public ClassVoyageController(ClassVoyageService classVoyageService) {
        this.classVoyageService = classVoyageService;
    }

    @Operation(summary = "Obtenir toutes les classes de voyage", description = "Récupère la liste de toutes les classes de voyage enregistrées.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClassVoyage.class))))
    })
    @GetMapping
    public ResponseEntity<Page<ClassVoyage>> getAllClassVoyages(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ClassVoyage> classVoyages = classVoyageService.findAll(page, size);
        return new ResponseEntity<>(classVoyages, HttpStatus.OK);
    }

    @Operation(summary = "Obtenir toutes les classes de voyage d'une agence de voyage", description = "Récupère la liste de toutes les classes de voyage enregistrées pour une agence de voyage specifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClassVoyage.class))))
    })
    @GetMapping("/agence/{idAgence}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ClassVoyage>> getAllClassVoyagesByAgence(@PathVariable UUID idAgence) {
        System.out.println("ID Agence: " + idAgence);
        List<ClassVoyage> classVoyages = classVoyageService.findAllForAgence(idAgence);
        System.out.println("ClassVoyages: " + classVoyages);
        return new ResponseEntity<>(classVoyages, HttpStatus.OK);
    }

    @Operation(summary = "Obtenir une classe de voyage par ID", description = "Récupère une classe de voyage en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe de voyage trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassVoyage.class))),
            @ApiResponse(responseCode = "404", description = "Classe de voyage non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassVoyage> getClassVoyageById(@PathVariable UUID id) {
        ClassVoyage classVoyage = classVoyageService.findById(id);
        if (classVoyage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(classVoyage, HttpStatus.OK);
    }

    @Operation(summary = "Créer une classe de voyage", description = "Ajoute une nouvelle classe de voyage à la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Classe de voyage créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassVoyage.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<ClassVoyage> createClassVoyage(@RequestBody ClassVoyageDTO classVoyageDTO) {
        ClassVoyage createdClassVoyage = classVoyageService.create(classVoyageDTO);
        return new ResponseEntity<>(createdClassVoyage, HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour une classe de voyage", description = "Modifie une classe de voyage existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Classe de voyage mise à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClassVoyage.class))),
            @ApiResponse(responseCode = "404", description = "Classe de voyage non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClassVoyage> updateClassVoyage(@PathVariable UUID id,
            @RequestBody ClassVoyageDTO classVoyageDTO) {
        if (classVoyageService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ClassVoyage updatedClassVoyage = classVoyageService.update(id, classVoyageDTO);
        return new ResponseEntity<>(updatedClassVoyage, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer une classe de voyage", description = "Supprime une classe de voyage en fonction de son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Classe de voyage supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Classe de voyage non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassVoyage(@PathVariable UUID id) {
        if (classVoyageService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        classVoyageService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

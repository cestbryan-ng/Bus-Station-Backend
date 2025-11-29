package com.enspy26.gi.plannification_voyage.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enspy26.gi.plannification_voyage.services.PassagerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
@RequestMapping("/passager")
public class PassagerController {

  private final PassagerService passagerService;

  /*
   * @Operation(summary = "Créer un nouveau passager", description =
   * "Permet de créer un passager en enregistrant ses informations dans la base de données. L'ID du passager est généré automatiquement."
   * )
   * 
   * @ApiResponses(value = {
   * 
   * @ApiResponse(responseCode = "201", description =
   * "Passager créé avec succès.", content = @Content(mediaType =
   * "application/json", schema = @Schema(implementation = Passager.class))),
   * 
   * @ApiResponse(responseCode = "400", description =
   * "Erreur, données invalides dans la requête.", content = @Content(mediaType =
   * "application/json", schema = @Schema(implementation = String.class))),
   * 
   * @ApiResponse(responseCode = "500", description =
   * "Erreur interne du serveur.", content = @Content(mediaType =
   * "application/json", schema = @Schema(implementation = String.class)))
   * })
   * 
   * @PostMapping()
   * public Passager ajouterPassager(@RequestBody PassagerDTO passagerDTO) {
   * return this.passagerService.create(passagerDTO);
   * }
   */
}

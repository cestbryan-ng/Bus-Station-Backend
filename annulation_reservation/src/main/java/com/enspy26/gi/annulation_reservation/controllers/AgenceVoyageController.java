package com.enspy26.gi.annulation_reservation.controllers;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.enspy26.gi.database_agence_voyage.dto.agence.AgenceVoyageDTO;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.annulation_reservation.services.AgenceVoyageService;
import com.enspy26.gi.annulation_reservation.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/agence")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AgenceVoyageController {

  private final UserService userService;
  private final AgenceVoyageService agenceVoyageService;

  @Operation(summary = "Create a new travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Travel agency created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or duplicate agency names"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PostMapping
  public Mono<ResponseEntity<AgenceVoyage>> createAgence(@Valid @RequestBody AgenceVoyageDTO agenceDTO) {
    return Mono.fromCallable(() -> {
      AgenceVoyage agence = userService.createAgenceVoyage(agenceDTO);
      return new ResponseEntity<>(agence, HttpStatus.CREATED);
    }).subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Update an existing travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Travel agency updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input or duplicate agency names"),
      @ApiResponse(responseCode = "404", description = "Agency not found")
  })
  @PatchMapping("/{id}")
  public Mono<ResponseEntity<AgenceVoyage>> updateAgence(@PathVariable("id") UUID agencyId,
      @RequestBody AgenceVoyageDTO agenceDTO) {
    return Mono.fromCallable(() -> {
      AgenceVoyage agence = userService.updateAgenceVoyage(agencyId, agenceDTO);
      return new ResponseEntity<>(agence, HttpStatus.OK);
    }).subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Delete a travel agency")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Travel agency deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Agency not found")
  })
  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteAgence(@PathVariable("id") UUID agencyId) {
    return Mono.fromRunnable(() -> userService.deleteAgenceVoyage(agencyId))
        .subscribeOn(Schedulers.boundedElastic())
        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        .onErrorResume(RuntimeException.class, e -> Mono.error(e));
  }

  @Operation(summary = "Retouner une agence d evoyage à partir de l'id du chef d'agence")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Agence trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgenceVoyage.class))),
      @ApiResponse(responseCode = "404", description = "Agence non trouvée")
  })
  @GetMapping("/chef-agence/{id}")
  public ResponseEntity<AgenceVoyage> getChefAgenceById(@PathVariable UUID id) {
    AgenceVoyage agence = this.agenceVoyageService.findAgenceByChefAgenceId(id);
    if (agence == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(agence, HttpStatus.OK);
  }
}

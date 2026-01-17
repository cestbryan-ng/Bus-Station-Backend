package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.dto.Payements.PayInResult;
import com.enspy26.gi.database_agence_voyage.dto.Payements.PayRequestDTO;
import com.enspy26.gi.database_agence_voyage.dto.Payements.ResultStatus;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationCancelDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationDetailDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationPreviewDTO;
import com.enspy26.gi.database_agence_voyage.dto.payment.SimulatePaymentRequestDTO;
import com.enspy26.gi.database_agence_voyage.dto.payment.SimulatePaymentResponseDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageCancelDTO;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.annulation_reservation.services.AnnulationService;
import com.enspy26.gi.annulation_reservation.services.ReservationService;
import com.enspy26.gi.annulation_reservation.exception.AnnulationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.enspy26.gi.annulation_reservation.configurations.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
@SecurityRequirement(name = "bearerAuth") // JWT requis pour tout le contrôleur
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final AnnulationService annulationService;

    @Operation(summary = "Obtenir toutes les réservations d'un utilisateur", description = "Récupère la liste de toutes les réservations d'un utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservationPreviewDTO.class)))),
            @ApiResponse(responseCode = "400", description = "données invalides.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/utilisateur/{idUser}")
    public Page<ReservationPreviewDTO> getAllReservationsForUser(@PathVariable UUID idUser,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        if (!SecurityUtils.getCurrentUserId().equals(idUser)) {
            new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Vous n'êtes pas autorisé à voir les réservations d'un autre");
        }

        return this.reservationService.findAllForUser(idUser, page, size);
    }

    @Operation(summary = "Obtenir une réservation et la liste de ses passagers par ID", description = "Récupère une réservation spécifique par ID Ainsi que la liste de tout ses passagers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation trouvée", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDetailDTO> getReservationById(@PathVariable UUID id) {
        ReservationDetailDTO reservation = reservationService.findById(id);
        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @Operation(summary = "Créer une réservation", description = "Ajoute une nouvelle réservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class))),
            @ApiResponse(responseCode = "404", description = "le voyage dont l'id est donnée n'existe pas"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping("/reserver")
    public ResponseEntity<Object> createReservation(@RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = this.reservationService.create(reservationDTO);

        return new ResponseEntity<>(reservation, HttpStatus.CREATED);

    }

    @Operation(summary = "Mettre à jour une réservation", description = "Modifie une réservation existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation mise à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class))),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable UUID id, @RequestBody Reservation reservation) {
        if (reservationService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reservation.setIdReservation(id);
        Reservation updatedReservation = reservationService.update(reservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @Operation(summary = "Supprimer une réservation", description = "Supprime une réservation par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
        if (reservationService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reservationService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Obtenir toutes les réservations d'une agence", description = "Récupère la liste de toutes les réservations pour les voyages d'une agence spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservationPreviewDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Agence non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/agence/{agenceId}")
    public ResponseEntity<?> getAllReservationsByAgence(
            @PathVariable UUID agenceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ReservationPreviewDTO> reservations = reservationService.findAllByAgenceId(agenceId, page, size);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des réservations pour l'agence {}: {}", agenceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des réservations de l'agence");
        }
    }

    @Operation(summary = "Annuler une réservation", description = "Permet d'annuler une réservation en par un utilisateur. Si l'annulation a lieu après confirmation, un coupon est généré.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation annulée avec succès.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Erreur, réservation non existante ou données invalides.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "reservation inexistante.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/annuler")
    public ResponseEntity<?> annulerReservation(
            @Parameter(description = "Données nécessaires pour annuler la réservation (ID de la réservation et informations supplémentaires).", required = true) @RequestBody ReservationCancelDTO reservationCancelDTO) {

        try {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            double risqueAnnulation = annulationService.annulerReservation(reservationCancelDTO, currentUserId);
            if (risqueAnnulation > 0) {
                return new ResponseEntity<>(risqueAnnulation, HttpStatus.OK);
            } else {
                return ResponseEntity.ok("Réservation annulée avec succès.");
            }
        } catch (AnnulationException e) {
            if (e.getMessage().contains("n'existe pas")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Operation(summary = "Payer une réservation", description = "Permet à un utilisateur de payer une réservation. En cas de succès, la réservation est considérée comme payée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réservation payée avec succès.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Erreur, données de paiement invalides ou problème avec le paiement.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Réservation inexistante ou non trouvée.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/payer")
    public ResponseEntity<?> payerReservation(
            @Parameter(description = "Données nécessaires pour payer la réservation (détails du paiement).", required = true) @RequestBody PayRequestDTO payRequestDTO) {
        // PayInResult payInResult =
        // this.payementService.pay(payRequestDTO.getMobilePhone(),
        // payRequestDTO.getMobilePhoneName(), payRequestDTO.getAmount(),
        // payRequestDTO.getUserId());
        PayInResult payInResult = this.reservationService.payerReservation(payRequestDTO);
        if (payInResult.getStatus() == ResultStatus.SUCCESS) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(payInResult.getErrors(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Annuler un voyage par une agence uniquement", description = "Permet d'annuler un voyage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voyage annulé avec succès.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Erreur, Voyage non existant ou données invalides.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Voyage inexistant.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/voyage/annuler")
    public ResponseEntity<?> annulerVoyage(VoyageCancelDTO voyageCancelDTO) {
        try {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            double risqueAnnulation = annulationService.annulerVoyage(voyageCancelDTO, currentUserId);
            if (risqueAnnulation > 0) {
                return new ResponseEntity<>(risqueAnnulation, HttpStatus.OK);
            } else {
                return ResponseEntity.ok("Voyage annulée avec succès.");
            }
        } catch (AnnulationException e) {
            if (e.getMessage().contains("n'existe pas")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

    }

    @Operation(
            summary = "Simuler un paiement (mode test)",
            description = "Permet de simuler un paiement sans passer par une vraie API de paiement. " +
                    "Utilisez 'simulate_success: true' pour simuler un succès, 'false' pour un échec. " +
                    "Si le montant payé atteint le prix total, la réservation passe en CONFIRMER."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Simulation de paiement effectuée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimulatePaymentResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Réservation déjà annulée ou confirmée"),
            @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    @PostMapping("/simulate-payment")
    public ResponseEntity<SimulatePaymentResponseDTO> simulatePayment(
            @Valid @RequestBody SimulatePaymentRequestDTO request) {
        SimulatePaymentResponseDTO response = reservationService.simulatePayment(request);
        return ResponseEntity.ok(response);
    }

}

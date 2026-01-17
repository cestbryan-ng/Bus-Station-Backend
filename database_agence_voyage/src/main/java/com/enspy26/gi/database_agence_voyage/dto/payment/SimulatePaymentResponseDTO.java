package com.enspy26.gi.database_agence_voyage.dto.payment;

import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutPayement;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SimulatePaymentResponseDTO {

    private boolean success;
    private String message;

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("reservation_id")
    private UUID reservationId;

    @JsonProperty("montant_paye")
    private double montantPaye;

    @JsonProperty("prix_total")
    private double prixTotal;

    @JsonProperty("reste_a_payer")
    private double resteAPayer;

    @JsonProperty("statut_reservation")
    private StatutReservation statutReservation;

    @JsonProperty("statut_payement")
    private StatutPayement statutPayement;

    @JsonProperty("is_fully_paid")
    private boolean isFullyPaid;
}
package com.enspy26.gi.database_agence_voyage.dto.payment;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SimulatePaymentRequestDTO {

    @NotNull(message = "L'ID de la réservation est obligatoire")
    @JsonProperty("reservation_id")
    private UUID reservationId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    // true = paiement réussi, false = paiement échoué
    @JsonProperty("simulate_success")
    private boolean simulateSuccess = true;
}
package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import com.enspy26.gi.database_agence_voyage.enums.PlaceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceReservationResponse {
  private int placeNumber; // Numéro de la place réservée
  private PlaceStatus status; // Statut de la place (libre ou réservée)
}

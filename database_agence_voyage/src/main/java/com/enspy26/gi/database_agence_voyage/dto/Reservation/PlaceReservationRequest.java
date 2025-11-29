package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import com.enspy26.gi.database_agence_voyage.enums.PlaceStatus;

import lombok.Data;

@Data
public class PlaceReservationRequest {
  private int placeNumber; // Numéro de la place à réserver
  private PlaceStatus status; // Statut de la place (libre ou réservée)
}

package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.database_agence_voyage.models.Voyage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReservationPreviewDTO {
  Reservation reservation;
  Voyage voyage;
  AgenceVoyage agence;
}

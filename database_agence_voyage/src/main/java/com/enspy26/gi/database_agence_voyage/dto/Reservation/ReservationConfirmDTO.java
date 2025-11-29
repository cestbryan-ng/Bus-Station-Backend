package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationConfirmDTO {
  private UUID idReservation;
  private double montantPaye;
}

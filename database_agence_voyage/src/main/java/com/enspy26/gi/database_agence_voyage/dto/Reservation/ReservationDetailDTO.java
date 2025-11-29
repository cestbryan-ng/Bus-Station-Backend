package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import java.util.List;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.Passager;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;
import com.enspy26.gi.database_agence_voyage.models.Voyage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationDetailDTO {
  private Reservation reservation;

  public ReservationDetailDTO(Reservation reservation) {
    this.reservation = reservation;
  }

  private List<Passager> passager;
  private Voyage voyage;
  private AgenceVoyage agence;
  private Vehicule vehicule;
}

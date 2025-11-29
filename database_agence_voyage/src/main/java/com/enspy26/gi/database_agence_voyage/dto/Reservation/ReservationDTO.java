package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.dto.PassagerDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
  private int nbrPassager;
  private double montantPaye;
  private UUID idUser;
  private UUID idVoyage;
  private PassagerDTO[] passagerDTO;
}

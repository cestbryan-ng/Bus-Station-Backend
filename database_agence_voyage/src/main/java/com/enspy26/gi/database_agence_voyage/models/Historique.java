package com.enspy26.gi.database_agence_voyage.models;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.enspy26.gi.database_agence_voyage.enums.StatutHistorique;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historique {
  @PrimaryKey
  private UUID idHistorique;
  private StatutHistorique statusHistorique;
  private Date dateReservation;
  private Date dateConfirmation;
  private Date dateAnnulation;
  private String causeAnnulation;
  private String origineAnnulation;
  private double tauxAnnulation;
  private double compensation;
  private UUID idReservation;
}

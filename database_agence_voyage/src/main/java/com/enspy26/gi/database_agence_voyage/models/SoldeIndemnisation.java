package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoldeIndemnisation {
  @PrimaryKey
  private UUID idSolde;
  private double solde;
  private String type;
  private UUID idUser;
  private UUID idAgenceVoyage;
}

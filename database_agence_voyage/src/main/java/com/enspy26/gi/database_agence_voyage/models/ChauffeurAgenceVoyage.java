package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.enspy26.gi.database_agence_voyage.enums.StatutChauffeur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChauffeurAgenceVoyage {
  @PrimaryKey
  private UUID chauffeurId;
  private UUID agenceVoyageId;
  private UUID userId;
  private StatutChauffeur statusChauffeur;
}

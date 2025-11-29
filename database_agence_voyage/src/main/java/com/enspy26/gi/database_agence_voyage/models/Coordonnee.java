package com.enspy26.gi.database_agence_voyage.models;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordonnee {
  @PrimaryKey
  private UUID idCoordonnee;
  private String latitude;
  private String longitude;
  private String altitude;
}

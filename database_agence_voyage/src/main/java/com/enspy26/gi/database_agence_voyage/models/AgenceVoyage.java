package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceVoyage {

  @PrimaryKey
  private UUID agencyId;

  private UUID organisationId;
  private UUID userId; // ID du ched d'agence
  private String longName;
  private String shortName;
  private String location;
  private String socialNetwork;
  private String description;
  private String greetingMessage;
  // private List<String> businessDomains;
}

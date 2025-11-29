package com.enspy26.gi.database_agence_voyage.models;

import java.time.LocalDateTime;
import java.util.List;
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
public class Organization {
  @PrimaryKey
  private UUID id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private UUID createdBy;
  private UUID updatedBy;
  private UUID organizationId;
  private List<UUID> businessDomains;
  private String email;
  private String shortName;
  private String longName;
  private String description;
  private String logoUrl;
  private boolean isIndividualBusiness;
  private String legalForm;
  private boolean isActive;
  private String websiteUrl;
  private String socialNetwork;
  private String businessRegistrationNumber;
  private String taxNumber;
  private Double capitalShare;
  private LocalDateTime registrationDate;
  private String ceoName;
  private LocalDateTime yearFounded;
  private List<String> keywords;
  private String status;
}

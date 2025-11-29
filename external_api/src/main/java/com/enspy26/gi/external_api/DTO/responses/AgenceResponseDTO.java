package com.enspy26.gi.external_api.DTO.responses;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class AgenceResponseDTO {
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private ZonedDateTime deletedAt;
  private UUID createdBy;
  private UUID updatedBy;
  private UUID organizationId;
  private UUID agencyId;
  private UUID ownerId;
  private String name;
  private String location;
  private String description;
  private boolean transferable;
  private List<UUID> businessDomains;
  private boolean isActive;
  private String logo;
  private String shortName;
  private String longName;
  private boolean isIndividualBusiness;
  private boolean isHeadquarter;
  private List<String> images;
  private String greetingMessage;
  private ZonedDateTime yearCreated;
  private String managerName;
  private ZonedDateTime registrationDate;
  private double averageRevenue;
  private double capitalShare;
  private String registrationNumber;
  private String socialNetwork;
  private String taxNumber;
  private List<String> keywords;
  private boolean isPublic;
  private boolean isBusiness;
  private OperationTimePlan operationTimePlan;
  private long totalAffiliatedCustomers;

  @Data
  public static class OperationTimePlan {
    private String additionalProp1;
    private String additionalProp2;
    private String additionalProp3;

    // Getters et setters
  }
}

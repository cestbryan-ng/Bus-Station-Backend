package com.enspy26.gi.database_agence_voyage.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {
  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

  @JsonProperty("deleted_at")
  private LocalDateTime deletedAt;

  @JsonProperty("created_by")
  private UUID createdBy;

  @JsonProperty("updated_by")
  private UUID updatedBy;

  @JsonProperty("organization_id")
  private UUID organizationId;

  @JsonProperty("business_domains")
  private List<UUID> businessDomains;

  private String email;

  @JsonProperty("short_name")
  private String shortName;

  @JsonProperty("long_name")
  private String longName;

  private String description;

  @JsonProperty("logo_url")
  private String logoUrl;

  @JsonProperty("is_individual_business")
  private boolean isIndividualBusiness;

  @JsonProperty("legal_form")
  private String legalForm;
  @JsonProperty("is_active")
  private boolean isActive;

  @JsonProperty("website_url")
  private String websiteUrl;

  @JsonProperty("social_network")
  private String socialNetwork;

  @JsonProperty("business_registration_number")
  private String businessRegistrationNumber;

  @JsonProperty("tax_number")
  private String taxNumber;

  @JsonProperty("capital_share")
  private Double capitalShare;

  @JsonProperty("registration_date")
  private LocalDateTime registrationDate;

  @JsonProperty("ceo_name")
  private String ceoName;

  @JsonProperty("year_founded")
  private LocalDateTime yearFounded;

  private List<String> keywords;

  private String status;

    /**
     * Factory method to create DTO from Organization entity
     *
     * @param organization Organization entity
     * @return OrganizationDto with organization information
     */
    public static OrganizationDto fromEntity(Organization organization) {
        OrganizationDto dto = new OrganizationDto();

        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        dto.setDeletedAt(organization.getDeletedAt());
        dto.setCreatedBy(organization.getCreatedBy());
        dto.setUpdatedBy(organization.getUpdatedBy());
        dto.setOrganizationId(organization.getOrganizationId());
        dto.setBusinessDomains(organization.getBusinessDomains());
        dto.setEmail(organization.getEmail());
        dto.setShortName(organization.getShortName());
        dto.setLongName(organization.getLongName());
        dto.setDescription(organization.getDescription());
        dto.setLogoUrl(organization.getLogoUrl());
        dto.setIndividualBusiness(organization.isIndividualBusiness());
        dto.setLegalForm(organization.getLegalForm());
        dto.setActive(organization.isActive());
        dto.setWebsiteUrl(organization.getWebsiteUrl());
        dto.setSocialNetwork(organization.getSocialNetwork());
        dto.setBusinessRegistrationNumber(organization.getBusinessRegistrationNumber());
        dto.setTaxNumber(organization.getTaxNumber());
        dto.setCapitalShare(organization.getCapitalShare());
        dto.setRegistrationDate(organization.getRegistrationDate());
        dto.setCeoName(organization.getCeoName());
        dto.setYearFounded(organization.getYearFounded());
        dto.setKeywords(organization.getKeywords());
        dto.setStatus(organization.getStatus());

        return dto;
    }
}

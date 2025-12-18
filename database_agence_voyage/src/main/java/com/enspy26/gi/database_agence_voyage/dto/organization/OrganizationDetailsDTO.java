package com.enspy26.gi.database_agence_voyage.dto.organization;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for detailed organization information
 * Used when retrieving complete organization profile
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class OrganizationDetailsDTO {

    private UUID id;

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("long_name")
    private String longName;

    @JsonProperty("short_name")
    private String shortName;

    private String email;
    private String description;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("website_url")
    private String websiteUrl;

    @JsonProperty("social_network")
    private String socialNetwork;

    @JsonProperty("legal_form")
    private String legalForm;

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

    @JsonProperty("is_individual_business")
    private boolean individualBusiness;

    @JsonProperty("is_active")
    private boolean active;

    private String status;

    @JsonProperty("business_domains")
    private List<UUID> businessDomains;

    private List<String> keywords;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_by")
    private UUID createdBy;

    @JsonProperty("updated_by")
    private UUID updatedBy;

    /**
     * Factory method to create DTO from Organization entity
     *
     * @param organization Organization entity
     * @return OrganizationDetailsDTO with all organization information
     */
    public static OrganizationDetailsDTO fromEntity(Organization organization) {
        OrganizationDetailsDTO dto = new OrganizationDetailsDTO();

        dto.setId(organization.getId());
        dto.setOrganizationId(organization.getOrganizationId());
        dto.setLongName(organization.getLongName());
        dto.setShortName(organization.getShortName());
        dto.setEmail(organization.getEmail());
        dto.setDescription(organization.getDescription());
        dto.setLogoUrl(organization.getLogoUrl());
        dto.setWebsiteUrl(organization.getWebsiteUrl());
        dto.setSocialNetwork(organization.getSocialNetwork());
        dto.setLegalForm(organization.getLegalForm());
        dto.setBusinessRegistrationNumber(organization.getBusinessRegistrationNumber());
        dto.setTaxNumber(organization.getTaxNumber());
        dto.setCapitalShare(organization.getCapitalShare());
        dto.setRegistrationDate(organization.getRegistrationDate());
        dto.setCeoName(organization.getCeoName());
        dto.setYearFounded(organization.getYearFounded());
        dto.setIndividualBusiness(organization.isIndividualBusiness());
        dto.setActive(organization.isActive());
        dto.setStatus(organization.getStatus());
        dto.setBusinessDomains(organization.getBusinessDomains());
        dto.setKeywords(organization.getKeywords());
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        dto.setCreatedBy(organization.getCreatedBy());
        dto.setUpdatedBy(organization.getUpdatedBy());

        return dto;
    }
}
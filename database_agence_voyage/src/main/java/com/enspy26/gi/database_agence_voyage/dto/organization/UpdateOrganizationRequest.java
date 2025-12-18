package com.enspy26.gi.database_agence_voyage.dto.organization;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating organization information
 * All fields are optional to support partial updates
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrganizationRequest {

    @Size(min = 3, max = 200, message = "Long name must be between 3 and 200 characters")
    @JsonProperty("long_name")
    private String longName;

    @Size(min = 2, max = 100, message = "Short name must be between 2 and 100 characters")
    @JsonProperty("short_name")
    private String shortName;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;

    @JsonProperty("business_domains")
    private List<UUID> businessDomains;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("legal_form")
    private String legalForm;

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
}
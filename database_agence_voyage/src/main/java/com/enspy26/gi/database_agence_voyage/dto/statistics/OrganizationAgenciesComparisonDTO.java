package com.enspy26.gi.database_agence_voyage.dto.statistics;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for organization agencies comparison
 * Contains performance metrics for each agency within an organization
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class OrganizationAgenciesComparisonDTO {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("total_agencies")
    private int totalAgencies;

    @JsonProperty("agencies")
    private List<AgencyComparisonDTO> agencies;

    @JsonProperty("best_performing_agency_id")
    private UUID bestPerformingAgencyId;

    @JsonProperty("best_performing_agency_name")
    private String bestPerformingAgencyName;
}
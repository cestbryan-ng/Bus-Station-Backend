package com.enspy26.gi.database_agence_voyage.dto.agence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for complete agency details
 * Contains all information about a travel agency
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class AgenceDetailsDTO {

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("organisation_id")
    private UUID organisationId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("long_name")
    private String longName;

    @JsonProperty("short_name")
    private String shortName;

    private String location;
    private String ville;

    @JsonProperty("social_network")
    private String socialNetwork;

    private String description;

    @JsonProperty("greeting_message")
    private String greetingMessage;

    @JsonProperty("statut_validation")
    private StatutValidation statutValidation;

    @JsonProperty("bsm_validator_id")
    private UUID bsmValidatorId;

    @JsonProperty("date_validation")
    private LocalDateTime dateValidation;

    @JsonProperty("motif_rejet")
    private String motifRejet;

    /**
     * Factory method to create details DTO from AgenceVoyage entity
     *
     * @param agence AgenceVoyage entity
     * @return AgenceDetailsDTO with complete agency information
     */
    public static AgenceDetailsDTO fromEntity(AgenceVoyage agence) {
        AgenceDetailsDTO dto = new AgenceDetailsDTO();

        dto.setAgencyId(agence.getAgencyId());
        dto.setOrganisationId(agence.getOrganisationId());
        dto.setUserId(agence.getUserId());
        dto.setLongName(agence.getLongName());
        dto.setShortName(agence.getShortName());
        dto.setLocation(agence.getLocation());
        dto.setVille(agence.getVille());
        dto.setSocialNetwork(agence.getSocialNetwork());
        dto.setDescription(agence.getDescription());
        dto.setGreetingMessage(agence.getGreetingMessage());
        dto.setStatutValidation(agence.getStatutValidation());
        dto.setBsmValidatorId(agence.getBsmValidatorId());
        dto.setDateValidation(agence.getDateValidation());
        dto.setMotifRejet(agence.getMotifRejet());

        return dto;
    }
}
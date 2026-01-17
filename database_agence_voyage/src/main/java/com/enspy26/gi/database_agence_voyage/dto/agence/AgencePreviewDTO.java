package com.enspy26.gi.database_agence_voyage.dto.agence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for agency preview in list view
 * Contains essential information for displaying agencies in a list
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class AgencePreviewDTO {

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
    private String description;

    @JsonProperty("greeting_message")
    private String greetingMessage;

    @JsonProperty("social_network")
    private String socialNetwork;

    @JsonProperty("statut_validation")
    private StatutValidation statutValidation;

    @JsonProperty("date_validation")
    private LocalDateTime dateValidation;

    /**
     * Factory method to create preview DTO from AgenceVoyage entity
     *
     * @param agence AgenceVoyage entity
     * @return AgencePreviewDTO with essential agency information
     */
    public static AgencePreviewDTO fromEntity(AgenceVoyage agence) {
        AgencePreviewDTO dto = new AgencePreviewDTO();

        dto.setAgencyId(agence.getAgencyId());
        dto.setOrganisationId(agence.getOrganisationId());
        dto.setUserId(agence.getUserId());
        dto.setLongName(agence.getLongName());
        dto.setShortName(agence.getShortName());
        dto.setLocation(agence.getLocation());
        dto.setVille(agence.getVille());
        dto.setDescription(agence.getDescription());
        dto.setGreetingMessage(agence.getGreetingMessage());
        dto.setSocialNetwork(agence.getSocialNetwork());
        dto.setStatutValidation(agence.getStatutValidation());
        dto.setDateValidation(agence.getDateValidation());

        return dto;
    }
}
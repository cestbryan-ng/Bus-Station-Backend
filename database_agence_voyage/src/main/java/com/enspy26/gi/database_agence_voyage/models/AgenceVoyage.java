package com.enspy26.gi.database_agence_voyage.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a travel agency
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Entity
@Table(name = "agencevoyage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceVoyage {

    @Id
    @Column(name = "agencyid")
    private UUID agencyId;

    @Column(name = "organisationid")
    private UUID organisationId;

    @Column(name = "userid")
    private UUID userId;

    @Column(name = "longname")
    private String longName;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "location")
    private String location;

    @Column(name = "socialnetwork")
    private String socialNetwork;

    @Column(name = "description")
    private String description;

    @Column(name = "greetingmessage")
    private String greetingMessage;

    @Column(name = "ville")
    private String ville;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_validation")
    private StatutValidation statutValidation;

    @Column(name = "bsm_validator_id")
    private UUID bsmValidatorId;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "motif_rejet")
    private String motifRejet;
}
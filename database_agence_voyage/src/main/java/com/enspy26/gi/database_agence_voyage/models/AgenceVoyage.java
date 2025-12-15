package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private UUID userId; // ID du chef d'agence

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
}

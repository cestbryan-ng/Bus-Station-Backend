package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "coordonnee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordonnee {

    @Id
    @Column(name = "idcoordonnee")
    private UUID idCoordonnee;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "altitude")
    private String altitude;
}

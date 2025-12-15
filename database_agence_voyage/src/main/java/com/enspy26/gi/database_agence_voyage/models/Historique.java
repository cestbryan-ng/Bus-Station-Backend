package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutHistorique;

@Entity
@Table(name = "historique")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historique {

    @Id
    @Column(name = "idhistorique")
    private UUID idHistorique;

    @Enumerated(EnumType.STRING)
    @Column(name = "statushistorique", nullable = false)
    private StatutHistorique statusHistorique;

    @Column(name = "datereservation")
    private Date dateReservation;

    @Column(name = "dateconfirmation")
    private Date dateConfirmation;

    @Column(name = "dateannulation")
    private Date dateAnnulation;

    @Column(name = "causeannulation", length = 255)
    private String causeAnnulation;

    @Column(name = "origineannulation", length = 255)
    private String origineAnnulation;

    @Column(name = "tauxannulation", nullable = false)
    private double tauxAnnulation;

    @Column(name = "compensation", nullable = false)
    private double compensation;

    @Column(name = "idreservation", nullable = false)
    private UUID idReservation;
}

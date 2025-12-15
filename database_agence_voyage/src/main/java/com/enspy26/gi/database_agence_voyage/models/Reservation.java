package com.enspy26.gi.database_agence_voyage.models;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.enspy26.gi.database_agence_voyage.enums.StatutPayement;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    private UUID idReservation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateReservation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true)
    private Date dateConfirmation;

    @Column(nullable = false)
    private int nbrPassager;

    @Column(nullable = false)
    private double prixTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statutReservation;

    @Column(nullable = false)
    private UUID idUser;

    @Column(nullable = false)
    private UUID idVoyage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private StatutPayement statutPayement;

    @Column(nullable = true)
    private String transactionCode;

    @Column(nullable = true)
    private double montantPaye;
}

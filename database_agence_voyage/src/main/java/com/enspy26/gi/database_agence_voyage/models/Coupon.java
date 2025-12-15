package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutCoupon;

@Entity
@Table(name = "coupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

    @Id
    @Column(name = "idcoupon")
    private UUID idCoupon;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datedebut")
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "datefin")
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statuscoupon")
    private StatutCoupon statusCoupon;

    @Column(name = "valeur")
    private double valeur;

    @Column(name = "idhistorique")
    private UUID idHistorique;

    @Column(name = "idsoldeindemnisation")
    private UUID idSoldeIndemnisation;
}

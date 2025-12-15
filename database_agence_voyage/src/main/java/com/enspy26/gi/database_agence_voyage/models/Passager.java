package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passager")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Passager {

    @Id
    private UUID idPassager;

    @Column(name = "numeropieceidentific")
    private String numeroPieceIdentific;

    private String nom;

    private String genre;

    private int age;

    @Column(name = "nbrbaggage")
    private int nbrBaggage;

    @Column(name = "idreservation")
    private UUID idReservation;

    @Column(name = "placechoisis")
    private int placeChoisis;
}

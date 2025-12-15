package com.enspy26.gi.database_agence_voyage.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "baggage")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Baggage {

    @Id
    @Column(name = "idbaggage")
    private UUID idBaggage;

    @Column(name = "nbrebaggage")
    private String nbreBaggage;

    @Column(name = "idpassager")
    private UUID idPassager;
}

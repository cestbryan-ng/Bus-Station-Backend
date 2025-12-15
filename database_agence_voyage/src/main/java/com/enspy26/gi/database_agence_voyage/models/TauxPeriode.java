package com.enspy26.gi.database_agence_voyage.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TauxPeriode implements Serializable {
    private Date dateDebut;
    private Date dateFin;
    private double taux;
    private double compensation; // c'est aussi un taux (valeur entre 0 et 1) qui est utilisé lorsque c'est
    // l'agence qui annule un voyage
}

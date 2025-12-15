package com.enspy26.gi.database_agence_voyage.models;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import com.enspy26.gi.database_agence_voyage.utils.DurationConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "politiqueannulation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolitiqueAnnulation {

    @Id
    private UUID idPolitique;

    @ElementCollection
    @Column(name = "listetauxperiode")
    private List<TauxPeriode> listeTauxPeriode;

    @Convert(converter = DurationConverter.class)
    @Column(name = "dureecoupon")
    private Duration dureeCoupon;

    @Column(name = "idagencevoyage")
    private UUID idAgenceVoyage;
}

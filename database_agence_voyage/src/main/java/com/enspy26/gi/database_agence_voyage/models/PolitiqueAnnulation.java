package com.enspy26.gi.database_agence_voyage.models;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.enspy26.gi.database_agence_voyage.utils.DurationConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolitiqueAnnulation {
  @PrimaryKey
  private UUID idPolitique;

  private List<TauxPeriode> listeTauxPeriode;

  @Convert(converter = DurationConverter.class)
  private Duration dureeCoupon;

  private UUID idAgenceVoyage;
}

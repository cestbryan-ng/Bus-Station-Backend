package com.enspy26.gi.database_agence_voyage.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassVoyage {
    @PrimaryKey
    private UUID idClassVoyage;
    private String nom;
    private double prix;
    private double tauxAnnulation;
    private UUID idAgenceVoyage;
}

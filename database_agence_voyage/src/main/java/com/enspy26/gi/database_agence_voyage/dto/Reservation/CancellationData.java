package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;
import com.enspy26.gi.database_agence_voyage.models.LigneVoyage;
import com.enspy26.gi.database_agence_voyage.models.PolitiqueAnnulation;
import com.enspy26.gi.database_agence_voyage.models.Voyage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellationData {
    private final Voyage voyage;
    private final LigneVoyage ligneVoyage;
    private final ClassVoyage classVoyage;
    private final PolitiqueAnnulation politiqueAnnulation;

    public CancellationData(Voyage voyage, LigneVoyage ligneVoyage,
                     ClassVoyage classVoyage, PolitiqueAnnulation politiqueAnnulation) {
        this.voyage = voyage;
        this.ligneVoyage = ligneVoyage;
        this.classVoyage = classVoyage;
        this.politiqueAnnulation = politiqueAnnulation;
    }
}
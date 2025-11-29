package com.enspy26.gi.database_agence_voyage.dto.Reservation;

import com.enspy26.gi.database_agence_voyage.models.Voyage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelByAgenceDTO {
    private String causeAnnulation;
    private String origineAnnulation;
    private UUID idReservation;
    private Voyage voyage;
    private boolean canceled;
}

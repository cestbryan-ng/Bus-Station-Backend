package com.enspy26.gi.annulation_reservation.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.enspy26.gi.annulation_reservation.services.ReservationService;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.PlaceReservationRequest;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.PlaceReservationResponse;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ReservationWebSocketController {

    private final ReservationService reservationService;

    // Quand un client tente de réserver une place
    @MessageMapping("/voyage/{voyageId}/reserver")
    @SendTo("/topic/voyage.{voyageId}")
    public List<PlaceReservationResponse> handleReservation(@DestinationVariable UUID voyageId,
                                                            PlaceReservationRequest request) {
        // Vérifier si la place est libre (à protéger avec un verrou, ou côté service)
        return reservationService.reservePlace(voyageId, request);

    }
}

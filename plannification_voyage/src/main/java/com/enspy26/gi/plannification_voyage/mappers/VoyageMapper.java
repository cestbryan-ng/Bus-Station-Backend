package com.enspy26.gi.plannification_voyage.mappers;

import com.enspy26.gi.database_agence_voyage.dto.Utilisateur.UserResponseDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageDetailsDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyagePreviewDTO;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;
import com.enspy26.gi.database_agence_voyage.models.Voyage;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class VoyageMapper {

    public VoyageDTO toVoyageDTO(Voyage voyage) {
        VoyageDTO voyageDTO = new VoyageDTO();
        voyageDTO.setTitre(voyage.getTitre());
        voyageDTO.setDescription(voyage.getDescription());
        voyageDTO.setDateDepartPrev(voyage.getDateDepartPrev());
        voyageDTO.setLieuDepart(voyage.getLieuDepart());
        voyageDTO.setLieuArrive(voyage.getLieuArrive());
        voyageDTO.setHeureDepartEffectif(voyage.getHeureDepartEffectif());
        voyageDTO.setDureeVoyage(voyage.getDureeVoyage());
        voyageDTO.setHeureArrive(voyage.getHeureArrive());
        voyageDTO.setDatePublication(voyage.getDatePublication());
        voyageDTO.setDateLimiteReservation(voyage.getDateLimiteReservation());
        voyageDTO.setDateLimiteConfirmation(voyage.getDateLimiteConfirmation());
        voyageDTO.setStatusVoyage(voyage.getStatusVoyage());
        voyageDTO.setSmallImage(voyage.getSmallImage());
        voyageDTO.setBigImage(voyage.getBigImage());

        if (voyage.getAmenities() != null)
            voyageDTO.setAmenities(voyage.getAmenities());
        else
            voyageDTO.setAmenities(List.of());

        return voyageDTO;
    }

    public VoyagePreviewDTO toVoyagePreviewDTO(Voyage voyage, AgenceVoyage agenceVoyage, ClassVoyage classVoyage) {
        VoyagePreviewDTO voyagePreviewDTO = new VoyagePreviewDTO();
        voyagePreviewDTO.setIdVoyage(voyage.getIdVoyage());
        voyagePreviewDTO.setNomAgence(agenceVoyage.getShortName()); // je suppose ici que le service c'est occupé de
                                                                    // verifier que
        // cette utilisateur est bien une agence
        voyagePreviewDTO.setLieuDepart(voyage.getLieuDepart());
        voyagePreviewDTO.setLieuArrive(voyage.getLieuArrive());
        voyagePreviewDTO.setNbrPlaceRestante(voyage.getNbrPlaceRestante());
        voyagePreviewDTO.setNbrPlaceReservable(voyage.getNbrPlaceReservable());
        voyagePreviewDTO.setDureeVoyage(voyage.getDureeVoyage());
        voyagePreviewDTO.setSmallImage(voyage.getSmallImage());
        voyagePreviewDTO.setBigImage(voyage.getBigImage());
        voyagePreviewDTO.setNomClasseVoyage(classVoyage.getNom());
        voyagePreviewDTO.setPrix(classVoyage.getPrix());
        voyagePreviewDTO.setDateDepartPrev(voyage.getDateDepartPrev());
        voyagePreviewDTO.setStatusVoyage(voyage.getStatusVoyage());
        if (voyage.getAmenities() != null)
            voyagePreviewDTO.setAmenities(voyage.getAmenities());
        else
            voyagePreviewDTO.setAmenities(List.of()); // Set to empty list if amenities are null
        return voyagePreviewDTO;
    }

    public VoyageDetailsDTO tovoyageDetailsDTO(Voyage voyage, AgenceVoyage agenceVoyage, ClassVoyage classVoyage,
            Vehicule vehicule,
            List<Integer> placesReservees, User chauffeur) {
        VoyageDetailsDTO voyageDetailsDTO = new VoyageDetailsDTO();
        voyageDetailsDTO.setIdVoyage(voyage.getIdVoyage());
        voyageDetailsDTO.setTitre(voyage.getTitre());
        voyageDetailsDTO.setDescription(voyage.getDescription());
        voyageDetailsDTO.setDateDepartPrev(voyage.getDateDepartPrev());
        voyageDetailsDTO.setLieuDepart(voyage.getLieuDepart());
        voyageDetailsDTO.setDateDepartEffectif(voyage.getDateDepartEffectif());
        voyageDetailsDTO.setDateArriveEffectif(voyage.getDateArriveEffectif());
        voyageDetailsDTO.setLieuArrive(voyage.getLieuArrive());
        voyageDetailsDTO.setHeureDepartEffectif(voyage.getHeureDepartEffectif());
        voyageDetailsDTO.setHeureArrive(voyage.getHeureArrive());
        voyageDetailsDTO.setDureeVoyage(voyage.getDureeVoyage());
        voyageDetailsDTO.setNbrPlaceReservable(voyage.getNbrPlaceReservable());
        voyageDetailsDTO.setNbrPlaceRestante(voyage.getNbrPlaceRestante());
        voyageDetailsDTO.setDatePublication(voyage.getDatePublication());
        voyageDetailsDTO.setDateLimiteConfirmation(voyage.getDateLimiteConfirmation());
        voyageDetailsDTO.setDateLimiteReservation(voyage.getDateLimiteReservation());
        voyageDetailsDTO.setStatusVoyage(voyage.getStatusVoyage());
        voyageDetailsDTO.setSmallImage(voyage.getSmallImage());
        voyageDetailsDTO.setBigImage(voyage.getBigImage());
        if (agenceVoyage != null)
            voyageDetailsDTO.setNomAgence(agenceVoyage.getLongName());
        voyageDetailsDTO.setPrix(classVoyage.getPrix());
        voyageDetailsDTO.setNomClasseVoyage(classVoyage.getNom());
        voyageDetailsDTO.setPointDeDepart(voyage.getPointDeDepart());
        voyageDetailsDTO.setPointArrivee(voyage.getPointArrivee());
        voyageDetailsDTO.setVehicule(vehicule);
        voyageDetailsDTO.setPlaceReservees(placesReservees);
        voyageDetailsDTO.setChauffeur(UserResponseDTO.fromUser(chauffeur));
        if (voyage.getAmenities() != null)
            voyageDetailsDTO.setAmenities(voyage.getAmenities());
        else
            voyageDetailsDTO.setAmenities(List.of());
        return voyageDetailsDTO;
    }
}

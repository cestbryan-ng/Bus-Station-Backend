package com.enspy26.gi.plannification_voyage.services;

import com.enspy26.gi.database_agence_voyage.dto.vehicule.VehiculeDTO;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;
import com.enspy26.gi.database_agence_voyage.repositories.VehiculeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehiculeService {
    private final VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public Page<Vehicule> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Vehicule> slice = vehiculeRepository.findAll(pageable);
        long total = vehiculeRepository.count();
        return PaginationUtils.SliceToPage(slice, total);
    }

    public Vehicule findById(UUID idVehicule) {
        return vehiculeRepository.findById(idVehicule).orElse(null);
    }

    public List<Vehicule> findByIdAgenceVoyage(UUID idAgenceVoyage) {
        List<Vehicule> slice = vehiculeRepository.findByIdAgenceVoyage(idAgenceVoyage);
        return slice;
    }

    public Vehicule create(VehiculeDTO vehiculeDTO) {

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(UUID.randomUUID());
        vehicule.setNom(vehiculeDTO.getNom());
        vehicule.setModele(vehiculeDTO.getModele());
        vehicule.setNbrPlaces(vehiculeDTO.getNbrPlaces());
        vehicule.setIdAgenceVoyage(vehiculeDTO.getIdAgenceVoyage());
        vehicule.setDescription(vehiculeDTO.getDescription());
        vehicule.setPlaqueMatricule(vehiculeDTO.getPlaqueMatricule());
        vehicule.setLienPhoto(vehiculeDTO.getLienPhoto());

        return vehiculeRepository.save(vehicule);
    }

    public Vehicule update(UUID id, VehiculeDTO vehiculeDTO) {
        Vehicule vehicule = vehiculeRepository.findById(id).orElse(null);
        if (vehicule != null) {
            vehicule.setNom(vehiculeDTO.getNom());
            vehicule.setModele(vehiculeDTO.getModele());
            vehicule.setNbrPlaces(vehiculeDTO.getNbrPlaces());
            vehicule.setIdAgenceVoyage(vehiculeDTO.getIdAgenceVoyage());
            return vehiculeRepository.save(vehicule);
        }
        return null;
    }

    public void delete(UUID idVehicule) {
        vehiculeRepository.deleteById(idVehicule);
    }
}

package com.enspy26.gi.plannification_voyage.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.enspy26.gi.database_agence_voyage.dto.classVoyage.ClassVoyageDTO;
import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.ClassVoyageRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ClassVoyageService {

    private final ClassVoyageRepository classVoyageRepository;

    public ClassVoyageService(ClassVoyageRepository classVoyageRepository) {
        this.classVoyageRepository = classVoyageRepository;
    }

    public Page<ClassVoyage> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<ClassVoyage> slice = classVoyageRepository.findAll(pageable);
        long total = classVoyageRepository.count();
        return PaginationUtils.SliceToPage(slice, total);
    }

    public ClassVoyage findById(UUID id) {
        return classVoyageRepository.findById(id).orElse(null);
    }

    public List<ClassVoyage> findAllForAgence(UUID idAgence) {
        List<ClassVoyage> slice = classVoyageRepository.findByIdAgenceVoyage(idAgence);
        return slice;
    }

    public ClassVoyage create(ClassVoyageDTO classVoyageDTO) {
        ClassVoyage classVoyage = new ClassVoyage();
        classVoyage.setIdClassVoyage(UUID.randomUUID());
        classVoyage.setNom(classVoyageDTO.getNom());
        classVoyage.setPrix(classVoyageDTO.getPrix());
        classVoyage.setTauxAnnulation(classVoyageDTO.getTauxAnnulation());
        classVoyage.setIdAgenceVoyage(classVoyageDTO.getIdAgenceVoyage());
        return classVoyageRepository.save(classVoyage);
    }

    public ClassVoyage update(UUID id, ClassVoyageDTO classVoyageDTO) {
        ClassVoyage classVoyage = classVoyageRepository.findById(id).orElse(null);
        if (classVoyage != null) {
            classVoyage.setNom(classVoyageDTO.getNom());
            classVoyage.setPrix(classVoyageDTO.getPrix());
            classVoyage.setTauxAnnulation(classVoyageDTO.getTauxAnnulation());
            classVoyage.setIdAgenceVoyage(classVoyageDTO.getIdAgenceVoyage());
            return classVoyageRepository.save(classVoyage);
        }
        return null;
    }

    public void delete(UUID id) {
        classVoyageRepository.deleteById(id);
    }

}

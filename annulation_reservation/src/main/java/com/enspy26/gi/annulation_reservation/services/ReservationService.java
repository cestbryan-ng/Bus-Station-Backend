package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.dto.PassagerDTO;
import com.enspy26.gi.database_agence_voyage.dto.Payements.PayInResult;
import com.enspy26.gi.database_agence_voyage.dto.Payements.PayRequestDTO;
import com.enspy26.gi.database_agence_voyage.dto.Payements.ResultStatus;
import com.enspy26.gi.database_agence_voyage.dto.Payements.StatusResult;
import com.enspy26.gi.database_agence_voyage.dto.Payements.TransactionStatus;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.PlaceReservationRequest;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.PlaceReservationResponse;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationConfirmDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationDetailDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationPreviewDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageDetailsDTO;
import com.enspy26.gi.database_agence_voyage.dto.BilletDTO;
import javax.persistence.EntityNotFoundException;

import com.enspy26.gi.database_agence_voyage.enums.PlaceStatus;
import com.enspy26.gi.database_agence_voyage.enums.RoleType;
import com.enspy26.gi.database_agence_voyage.enums.StatutHistorique;
import com.enspy26.gi.database_agence_voyage.enums.StatutPayement;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.enspy26.gi.database_agence_voyage.models.*;
import com.enspy26.gi.database_agence_voyage.repositories.*;
import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import com.enspy26.gi.notification.factory.NotificationFactory;
import com.enspy26.gi.notification.models.NotificationEvent;
import com.enspy26.gi.notification.services.NotificationService;
import com.enspy26.gi.plannification_voyage.services.VoyageService;
import com.enspy26.gi.external_api.services.PayementService;
import com.enspy26.gi.annulation_reservation.utils.PaginationUtils;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HistoriqueRepository historiqueRepository;
    private final VoyageRepository voyageRepository;
    private final LigneVoyageRepository ligneVoyageRepository;
    private final ClassVoyageRepository classVoyageRepository;
    private final PassagerRepository passagerRepository;
    private final UserRepository userRepository;
    private final VoyageService voyageService;
    private final PayementService payementService;
    private final VehiculeRepository vehiculeRepository;
    private final AgenceVoyageRepository agenceVoyageRepository;
    private final NotificationService notificationService;

    public Page<Reservation> findAll(int page, int size) {
        // TODO, ajouter un filtre pour ne retourner que les voyages qui ne sont pas
        // déjà passé
        Pageable pageable = PageRequest.of(page, size);
        Slice<Reservation> slice = reservationRepository.findAll(pageable);
        long total = reservationRepository.count();
        return PaginationUtils.SliceToPage(slice, total);
    }

    public Page<ReservationPreviewDTO> findAllForUser(UUID idUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("L'utilisateur dont l'id est spécifié n'existe pas."));
        if (user.getRole().contains(RoleType.AGENCE_VOYAGE)) {
            throw new RuntimeException(
                    "L'utilisateur dont l'id est spécifié est une agence de voyage et ne peut donc pas réservé");
        }
        List<Reservation> reservations = reservationRepository.findByIdUser(idUser);
        List<ReservationPreviewDTO> reservationPreviewDTOs = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Voyage voyage = voyageRepository.findById(reservation.getIdVoyage()).orElse(null);
            AgenceVoyage agence = agenceVoyageRepository
                    .findById(this.ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage())
                            .getIdAgenceVoyage())
                    .orElseThrow(() -> new RuntimeException("Le voyage dont l'id est spécifique n'existe pas."));
            ReservationPreviewDTO reservationPreviewDTO = new ReservationPreviewDTO(reservation, voyage, agence);
            reservationPreviewDTOs.add(reservationPreviewDTO);
        }
        long total = reservationRepository.count();
        log.info("Nombre total de réservations pour l'utilisateur {}: {}", idUser, total);
        return PaginationUtils.ContentToPage(reservationPreviewDTOs, pageable, total);
    }

    public ReservationDetailDTO findById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La reservation dont l'id est spécifique n'existe pas."));
        ReservationDetailDTO reservationDetailDTO = new ReservationDetailDTO(reservation);
        // On charge les passager dans la liste
        reservationDetailDTO.setPassager(passagerRepository.findAllByIdReservation(id));
        // On charge le voyage
        reservationDetailDTO.setVoyage(voyageRepository.findById(reservation.getIdVoyage())
                .orElseThrow(() -> new RuntimeException("Le voyage dont l'id est spécifié n'existe pas.")));
        // On charge l'agence
        reservationDetailDTO.setAgence(agenceVoyageRepository
                .findById(this.ligneVoyageRepository.findByIdVoyage(reservationDetailDTO.getVoyage().getIdVoyage())
                        .getIdAgenceVoyage())
                .orElseThrow(() -> new RuntimeException("Le voyage dont l'id est spécifique n'existe pas.")));
        // On charge le vehicule
        reservationDetailDTO.setVehicule(vehiculeRepository
                .findById(this.ligneVoyageRepository.findByIdVoyage(reservationDetailDTO.getVoyage().getIdVoyage())
                        .getIdVehicule())
                .orElseThrow(() -> new RuntimeException("Le vehicule dont l'id est spécifique n'existe pas.")));
        return reservationDetailDTO;
    }

    public Reservation create(ReservationDTO reservationDTO) {
        // Récupérer la date et l'heure actuelles
        Date now = new Date();

        // Vérifier si l'utilisteur existe
        User user = userRepository.findById(reservationDTO.getIdUser())
                .orElseThrow(() -> new RuntimeException("L'utilisateur dont l'id est spécifique n'existe pas."));

        // Vérifier si le voyage existe
        Voyage voyage = voyageRepository.findById(reservationDTO.getIdVoyage())
                .orElseThrow(() -> new RuntimeException("Le voyage dont l'id est spécifié n'existe pas."));

        // Verifier que la liste des passagers est non vide
        if (reservationDTO.getPassagerDTO().length <= 0) {
            throw new RuntimeException("La liste des passagers est vide.");
        }

        // Verifier que les place choisis sont disponibles et correctes
        VoyageDetailsDTO voyageDetailsDTO = voyageService.findById(reservationDTO.getIdVoyage());
        for (PassagerDTO i : reservationDTO.getPassagerDTO()) {
            if (voyageDetailsDTO.getPlaceReservees().contains(i.getPlaceChoisis())) {
                throw new RuntimeException("Une des places choisis est déjà reservée.");
            }
        }
        // Vérifier que la date actuelle est inférieure à la date limite de reservation
        // du voyage
        if (now.after(voyage.getDateLimiteReservation())) {
            throw new RuntimeException(
                    "La date de réservation doit être antérieure à la date limite de reservation du voyage.");
        }

        // Vérifier qu'il y a suffisamment de places reservable
        if (voyage.getNbrPlaceReservable() < reservationDTO.getNbrPassager()) {
            throw new RuntimeException("Il n'y a pas suffisamment de places disponibles pour ce voyage.");
        }

        // verifier que le nombre de passager donné est égale à la taille de la liste de
        // passager
        if (reservationDTO.getNbrPassager() != reservationDTO.getPassagerDTO().length) {
            throw new RuntimeException("Il doit avoir autant de passager que le nombre de passager specifier");
        }

        LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());
        ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage()).orElse(null);

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setIdReservation(UUID.randomUUID());
        reservation.setDateReservation(now);
        reservation.setStatutReservation(StatutReservation.RESERVER);
        reservation.setIdUser(reservationDTO.getIdUser());
        reservation.setIdVoyage(reservationDTO.getIdVoyage());
        reservation.setNbrPassager(reservationDTO.getNbrPassager());
        reservation.setMontantPaye(reservationDTO.getMontantPaye());
        reservation.setStatutPayement(StatutPayement.NO_PAYMENT);
        reservation.setPrixTotal(reservationDTO.getNbrPassager() * classVoyage.getPrix());

        // On cree les passagers
        for (int i = 0; i < reservationDTO.getPassagerDTO().length; i++) {
            Passager passager = new Passager();
            passager.setIdPassager(UUID.randomUUID());
            passager.setNumeroPieceIdentific(reservationDTO.getPassagerDTO()[i].getNumeroPieceIdentific());
            passager.setNom(reservationDTO.getPassagerDTO()[i].getNom());
            passager.setGenre(reservationDTO.getPassagerDTO()[i].getGenre());
            passager.setAge(reservationDTO.getPassagerDTO()[i].getAge());
            passager.setNbrBaggage(reservationDTO.getPassagerDTO()[i].getNbrBaggage());
            passager.setPlaceChoisis(reservationDTO.getPassagerDTO()[i].getPlaceChoisis());
            passager.setIdReservation(reservation.getIdReservation());
            passagerRepository.save(passager);
        }

        // Mettre à jour le nombre de places réservées et réservables
        voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() + reservation.getNbrPassager());
        voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() - reservation.getNbrPassager());
        voyageRepository.save(voyage);
        // On crée l'historique
        Historique historique = new Historique();
        historique.setIdHistorique(UUID.randomUUID());
        historique.setDateReservation(now);
        historique.setIdReservation(reservation.getIdReservation());
        historique.setStatusHistorique(StatutHistorique.VALIDER);
        this.historiqueRepository.save(historique);

        // Envoyer notifications de création de réservation
        try {
            // Notifier l'utilisateur
            notificationService.sendNotification(
                    NotificationEvent.builder()
                            .type(NotificationType.RESERVATION_CREATED)
                            .recipientType(RecipientType.USER)
                            .recipientId(reservation.getIdUser())
                            .variables(Map.of(
                                    "reservationId", reservation.getIdReservation().toString(),
                                    "voyageTitle", voyage.getTitre(),
                                    "passengerCount", reservation.getNbrPassager(),
                                    "totalAmount", reservation.getPrixTotal()))
                            .build());

            // Notifier l'agence
            notificationService.sendNotification(
                    NotificationFactory.createReservationCreatedEvent(reservation, voyage,
                            ligneVoyage.getIdAgenceVoyage()));
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi des notifications de réservation: {}", e.getMessage());
        }

        // Enregistrer la réservation
        Reservation reserv = reservationRepository.save(reservation);
        log.info("La reservation a été créée avec succès pour l'utilisateur {} pour le voyage {}",
                reservation.getIdUser(), reservation.getIdVoyage());
        return reserv;

    }

    public Reservation update(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(UUID id) {
        reservationRepository.deleteById(id);
    }

    private Reservation confirmerReservation(ReservationConfirmDTO reservationConfirmDTO) {
        // Récupérer la date et l'heure actuelles
        Date now = new Date();

        // On verifie que la reservation existe la reservation
        Reservation reservation = this.reservationRepository.findById(reservationConfirmDTO.getIdReservation())
                .orElseThrow(() -> new RuntimeException("La Reservation n'existe pas"));

        // On récupère l'historique
        Historique historique = historiqueRepository.findByIdReservation(reservation.getIdReservation()).orElseThrow(
                () -> new RuntimeException("L'Historique associé à la reservation n'existe pas"));

        Voyage voyage = voyageRepository.findById(reservation.getIdVoyage())
                .orElseThrow(() -> new RuntimeException("Le voyage associé n'existe pas"));

        // Vérifier que la date actuelle est inférieure à la date limite de confirmation
        // du voyage
        if (now.after(voyage.getDateLimiteConfirmation())) {
            throw new RuntimeException(
                    "La date de confirmation doit être antérieure à la date limite de confirmation du voyage.");
        }

        // Vérifier qu'il y a suffisamment de places restante
        if (voyage.getNbrPlaceRestante() < reservation.getNbrPassager()) {
            throw new RuntimeException("Il n'y a pas suffisamment de places libre pour confirmation");
        }

        // verifier que le prix total a été payé
        if (reservation.getMontantPaye() + reservationConfirmDTO.getMontantPaye() < reservation.getPrixTotal()) {
            throw new RuntimeException("Le prix total pour le voyage n'est pas complet");
        }

        reservation.setDateConfirmation(now);
        reservation.setMontantPaye(reservationConfirmDTO.getMontantPaye() + reservation.getMontantPaye());
        reservation.setStatutReservation(StatutReservation.CONFIRMER);
        // gestion des places
        voyage.setNbrPlaceRestante(voyage.getNbrPlaceRestante() - reservation.getNbrPassager());
        voyage.setNbrPlaceConfirm(voyage.getNbrPlaceConfirm() + reservation.getNbrPassager());
        this.voyageRepository.save(voyage);

        historique.setDateConfirmation(now);
        historique.setStatusHistorique(StatutHistorique.VALIDER);
        this.historiqueRepository.save(historique);

        // Envoyer notification de confirmation
        try {
            notificationService.sendNotification(
                    NotificationFactory.createReservationConfirmedEvent(reservation, voyage));
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de la notification de confirmation: {}", e.getMessage());
        }

        return this.reservationRepository.save(reservation);
    }

    public PayInResult payerReservation(PayRequestDTO payRequestDTO) {

        // On se rassure que la reservation existe
        Reservation reservation = this.reservationRepository.findById(payRequestDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation non existante"));

        PayInResult payInResult = this.payementService.pay(payRequestDTO.getMobilePhone(),
                payRequestDTO.getMobilePhoneName(), payRequestDTO.getAmount(),
                payRequestDTO.getUserId());

        if (payInResult.getStatus() == ResultStatus.SUCCESS) {
            // On sérialise dans la BD
            reservation.setStatutPayement(StatutPayement.PENDING);
            reservation.setTransactionCode(payInResult.getData().getTransaction_code());
            this.reservationRepository.save(reservation);
        }
        return payInResult;
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void verifierPayement() {
        List<Reservation> reservations = reservationRepository.findAll();
        // TODO Faire en sorte que la liste retourné soit filtré par le status PENDING
        for (Reservation reservation : reservations) {
            // Si la reservation a un status de PENDING alors on regarde s'il a déjà été
            // payé
            if (reservation.getStatutPayement() == StatutPayement.PENDING) {
                StatusResult statusResult = this.payementService.payStatus(reservation.getTransactionCode());
                if (statusResult.getData().getStatus() == TransactionStatus.COMPLETED) {
                    reservation.setStatutPayement(StatutPayement.PAID);
                    reservation.setMontantPaye(
                            reservation.getMontantPaye() + statusResult.getData().getTransaction_amount());
                    // Envoyer notification de paiement reçu
                    try {
                        notificationService.sendNotification(
                                NotificationFactory.createPaymentReceivedEvent(reservation));
                    } catch (Exception e) {
                        log.warn("Erreur lors de l'envoi de la notification de paiement: {}", e.getMessage());
                    }
                    if (reservation.getMontantPaye() >= reservation.getPrixTotal()) {
                        confirmerReservation(new ReservationConfirmDTO(reservation.getIdReservation(),
                                reservation.getMontantPaye()));
                    }
                    System.out.print("Voici le result" + statusResult.toString());
                } else if (statusResult.getStatus() == ResultStatus.FAILED) {
                    reservation.setStatutPayement(StatutPayement.FAILED);
                    try {
                        notificationService.sendNotification(
                                NotificationFactory.createPaymentFailedEvent(reservation));
                    } catch (Exception e) {
                        log.warn("Erreur lors de l'envoi de la notification d'échec de paiement: {}", e.getMessage());
                    }

                }
                reservationRepository.save(reservation);

            }
        }
    }

    public BilletDTO informationPourBillet(UUID idPassager) {
        // Récupérer le passager
        Passager passager = passagerRepository.findById(idPassager)
                .orElseThrow(() -> new EntityNotFoundException("Le passager dont l'id est spécifique n'existe pas."));

        // Récupérer la réservation du passager
        ReservationDetailDTO reservation = this.findById(passager.getIdReservation());
        if (reservation == null) {
            throw new EntityNotFoundException("La réservation dont l'id est spécifique n'existe pas.");
        }

        // Récupérer les détails du voyage associés à la réservation
        VoyageDetailsDTO voyage = voyageService.findById(reservation.getReservation().getIdVoyage());
        if (voyage == null) {
            throw new EntityNotFoundException("Le voyage dont l'id est spécifique n'existe pas.");
        }

        // Construction du billet
        BilletDTO billet = new BilletDTO();
        billet.setTitre(voyage.getTitre());
        billet.setDescription(voyage.getDescription());
        billet.setDateDepartPrev(voyage.getDateDepartPrev());
        billet.setLieuDepart(voyage.getLieuDepart());
        billet.setDateDepartEffectif(voyage.getDateDepartEffectif());
        billet.setDateArriveEffectif(voyage.getDateArriveEffectif());
        billet.setLieuArrive(voyage.getLieuArrive());
        billet.setHeureDepartEffectif(voyage.getHeureDepartEffectif());
        billet.setHeureArrive(voyage.getHeureArrive());
        billet.setDureeVoyage(voyage.getDureeVoyage());
        billet.setStatusVoyage(voyage.getStatusVoyage());
        billet.setSmallImage(voyage.getSmallImage());
        billet.setBigImage(voyage.getBigImage());
        billet.setNomAgence(voyage.getNomAgence());
        billet.setPrix(voyage.getPrix());
        billet.setNomClasseVoyage(voyage.getNomClasseVoyage());
        billet.setPointDeDepart(voyage.getPointDeDepart());
        billet.setPointArrivee(voyage.getPointArrivee());
        billet.setNom(passager.getNom());
        billet.setGenre(passager.getGenre());
        billet.setAge(passager.getAge());
        billet.setNbrBaggage(passager.getNbrBaggage());
        billet.setNumeroPieceIdentific(passager.getNumeroPieceIdentific());

        return billet;
    }

    public Page<ReservationPreviewDTO> findAllByAgenceId(UUID agenceId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Récupérer toutes les lignes de voyage pour l'agence
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        if (lignesVoyage.isEmpty()) {
            return PaginationUtils.ContentToPage(new ArrayList<>(), pageable, 0);
        }

        // Extraire les IDs des voyages de l'agence
        List<UUID> voyageIds = lignesVoyage.stream()
                .map(LigneVoyage::getIdVoyage)
                .collect(Collectors.toList());

        // Récupérer toutes les réservations pour ces voyages
        List<Reservation> allReservations = new ArrayList<>();
        for (UUID voyageId : voyageIds) {
            List<Reservation> reservationsForVoyage = reservationRepository.findByIdVoyage(voyageId);
            allReservations.addAll(reservationsForVoyage);
        }

        // Calculer la pagination
        int start = page * size;
        int end = Math.min(start + size, allReservations.size());
        List<Reservation> pagedReservations = allReservations.subList(start, end);

        // Mapper vers ReservationPreviewDTO
        List<ReservationPreviewDTO> reservationPreviewDTOs = new ArrayList<>();
        for (Reservation reservation : pagedReservations) {
            try {
                Voyage voyage = voyageRepository.findById(reservation.getIdVoyage()).orElse(null);
                if (voyage != null) {
                    AgenceVoyage agence = agenceVoyageRepository.findById(agenceId).orElse(null);
                    if (agence != null) {
                        ReservationPreviewDTO reservationPreviewDTO = new ReservationPreviewDTO(reservation, voyage,
                                agence);
                        reservationPreviewDTOs.add(reservationPreviewDTO);
                    }
                }
            } catch (Exception e) {
                // Log l'erreur et continuer avec les autres réservations
                System.err.println("Erreur lors du traitement de la réservation " + reservation.getIdReservation()
                        + ": " + e.getMessage());
            }
        }

        long total = allReservations.size();
        return PaginationUtils.ContentToPage(reservationPreviewDTOs, pageable, total);
    }

    private final Map<UUID, Set<Integer>> reservedMap = new ConcurrentHashMap<>();

    public synchronized List<PlaceReservationResponse> reservePlace(UUID voyageId, PlaceReservationRequest request) {
        VoyageDetailsDTO voyageDetailsDTO = voyageService.findById(voyageId);

        Set<Integer> occupied = new HashSet<>(voyageDetailsDTO.getPlaceReservees());

        // Récupère ou initialise la liste des places réservées via WebSocket
        Set<Integer> reserved = reservedMap.computeIfAbsent(voyageId, id -> new HashSet<>());

        int place = request.getPlaceNumber();
        PlaceStatus status = request.getStatus();

        // Tentative de réserver une place déjà occupée (BD)
        if (status == PlaceStatus.RESERVED) {
            if (occupied.contains(place) || reserved.contains(place)) {
                return null; // Indique une erreur
            }
            reserved.add(place);
        }

        // Libération d'une place
        else if (status == PlaceStatus.FREE) {
            // On ne peut pas libérer une place OCCUPIED
            if (occupied.contains(place)) {
                return null; // Interdiction de libérer une place en base
            }
            reserved.remove(place); // Libération
        }

        // Met à jour la map
        reservedMap.put(voyageId, reserved);

        // Génère la liste complète : occupied + reserved
        Set<Integer> allPlaces = new HashSet<>();
        allPlaces.addAll(occupied);
        allPlaces.addAll(reserved);

        List<PlaceReservationResponse> responses = new ArrayList<>();

        for (Integer p : allPlaces) {
            PlaceReservationResponse resp = new PlaceReservationResponse();
            resp.setPlaceNumber(p);

            if (occupied.contains(p)) {
                resp.setStatus(PlaceStatus.OCCUPIED);
            } else if (reserved.contains(p)) {
                resp.setStatus(PlaceStatus.RESERVED);
            }
            responses.add(resp);
        }

        return responses;
    }

}

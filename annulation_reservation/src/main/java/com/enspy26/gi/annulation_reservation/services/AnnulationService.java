package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.dto.Reservation.CancellationData;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationCancelByAgenceDTO;
import com.enspy26.gi.database_agence_voyage.dto.Reservation.ReservationCancelDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageCancelDTO;
import com.enspy26.gi.database_agence_voyage.enums.*;
import com.enspy26.gi.database_agence_voyage.models.*;
import com.enspy26.gi.database_agence_voyage.repositories.*;
import com.enspy26.gi.annulation_reservation.utils.AnnulationOperator;
import com.enspy26.gi.annulation_reservation.exception.AnnulationException;
import com.enspy26.gi.notification.enums.NotificationType;
import com.enspy26.gi.notification.enums.RecipientType;
import com.enspy26.gi.notification.factory.NotificationFactory;
import com.enspy26.gi.notification.models.NotificationEvent;
import com.enspy26.gi.notification.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AnnulationService {
    private final ReservationRepository reservationRepository;
    private final VoyageRepository voyageRepository;
    private final HistoriqueRepository historiqueRepository;
    private final LigneVoyageRepository ligneVoyageRepository;
    private final ClassVoyageRepository classVoyageRepository;
    private final PassagerRepository passagerRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PolitiqueAnnulationRepository politiqueAnnulationRepository;
    private final SoldeIndemnisationRepository soldeIndemnisationRepository;
    private final NotificationService notificationService;
    private final VehiculeRepository vehiculeRepository;

    @Transactional
    public double annulerVoyage(VoyageCancelDTO voyageCancelDTO, UUID userId) {
        // Vérification d'autorisation
        if (!isAuthorizedToManageTravel(userId, voyageCancelDTO.getIdVoyage())) {
            throw new AnnulationException("Vous n'êtes pas autorisé à annuler ce voyage", HttpStatus.FORBIDDEN);
        }

        // Récupération du voyage avec vérification d'existence
        Voyage voyage = voyageRepository.findById(voyageCancelDTO.getIdVoyage())
                .orElseThrow(() -> new AnnulationException("Le voyage spécifié n'existe pas", HttpStatus.NOT_FOUND));

        // on cherche la liste des reservations de ce voyage
        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());

        if (reservations.isEmpty()) {
            log.info("Aucune réservation trouvée pour le voyage {}", voyage.getIdVoyage());
            return 0.0;
        }

        double risque = 0.0;
        List<UUID> affectedUserIds = new ArrayList<>();

        for (Reservation reservation : reservations) {
            affectedUserIds.add(reservation.getIdUser());
            // on effectue l'annulation
            ReservationCancelByAgenceDTO reservationCancelByAgenceDTO = new ReservationCancelByAgenceDTO();
            reservationCancelByAgenceDTO.setVoyage(voyage);
            reservationCancelByAgenceDTO.setIdReservation(reservation.getIdReservation());
            reservationCancelByAgenceDTO.setCanceled(voyageCancelDTO.isCanceled());
            reservationCancelByAgenceDTO.setCauseAnnulation(voyageCancelDTO.getCauseAnnulation());
            reservationCancelByAgenceDTO.setOrigineAnnulation(voyageCancelDTO.getOrigineAnnulation());

            risque += this.annulerReservationByAgence(reservationCancelByAgenceDTO, userId);

        }

        // Libération du véhicule si annulation complète
        if (voyageCancelDTO.isCanceled()) {
            libererVehicule(voyage.getIdVoyage());
            //Envoyer notifications d'annulation de voyage
            try {
                notificationService.sendNotification(
                        NotificationFactory.createVoyageCancelledEvent(voyage, voyageCancelDTO.getAgenceVoyageId(), RecipientType.AGENCY)
                );

                // Notifier tous les utilisateurs affectés
                for (UUID affectedUserId : affectedUserIds) {
                    notificationService.sendNotification(
                            NotificationFactory.createVoyageCancelledEvent(voyage, affectedUserId, RecipientType.USER)
                    );
                }
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi des notifications d'annulation de voyage: {}", e.getMessage());
            }
        }

        // changement du status du voyage
        voyage.setStatusVoyage(StatutVoyage.ANNULE);
        voyageRepository.save(voyage);

        log.info("Voyage {} annulé avec succès. Risque total: {}", voyage.getIdVoyage(), risque);
        return risque;
    }

    @Transactional
    public double annulerReservation(ReservationCancelDTO reservationCancelDTO, UUID userId) {
        // Récupérer la date et l'heure actuelles
        Date now = new Date();

        // Récupération et vérification de la réservation
        Reservation reservation = reservationRepository.findById(reservationCancelDTO.getIdReservation())
                .orElseThrow(() -> new AnnulationException("La réservation spécifiée n'existe pas", HttpStatus.NOT_FOUND));

        // Vérification d'autorisation - seul le propriétaire peut annuler sa réservation
        if (!reservation.getIdUser().equals(userId)) {
            throw new AnnulationException("Vous n'êtes pas autorisé à annuler cette réservation", HttpStatus.FORBIDDEN);
        }

        // Vérification du statut de la réservation
        if (reservation.getStatutReservation() == StatutReservation.ANNULER) {
            throw new AnnulationException("Cette réservation est déjà annulée", HttpStatus.CONFLICT);
        }

        // Récupération de l'historique
        Historique historique = historiqueRepository.findByIdReservation(reservation.getIdReservation())
                .orElseThrow(() -> new AnnulationException("L'historique associé à la réservation n'existe pas", HttpStatus.INTERNAL_SERVER_ERROR));

        // Vérification des passagers
        List<Passager> passagersReservation = passagerRepository.findAllByIdReservation(reservation.getIdReservation());
        if (passagersReservation.size() < reservationCancelDTO.getIdPassagers().length) {
            throw new AnnulationException("Le nombre de passagers à annuler dépasse le nombre total de passagers", HttpStatus.BAD_REQUEST);
        }

        // Récupération optimisée des données nécessaires
        CancellationData cancellationData = getCancellationData(reservation.getIdVoyage());

        double tauxAnnulation = AnnulationOperator.tauxannualtion(
                cancellationData.getClassVoyage(),
                cancellationData.getPolitiqueAnnulation(),
                cancellationData.getVoyage().getDateLimiteReservation(),
                cancellationData.getVoyage().getDateLimiteConfirmation(),
                now
        );

        // Si ce n'est qu'une simulation pour voir le risque, retourner le taux
        if (!reservationCancelDTO.isCanceled()) {
            return tauxAnnulation;
        }

        // Traitement de l'annulation effective
        return processReservationCancellation(reservation, reservationCancelDTO, historique,
                cancellationData, tauxAnnulation, now);
    }

    @Transactional
    public double annulerReservationByAgence(ReservationCancelByAgenceDTO reservationCancelDTO, UUID userId) {
        // Récupérer la date et l'heure actuelles
        Date now = new Date();

        // Récupération de la réservation
        Reservation reservation = reservationRepository.findById(reservationCancelDTO.getIdReservation())
                .orElseThrow(() -> new AnnulationException("La réservation spécifiée n'existe pas", HttpStatus.NOT_FOUND));

        // Vérification d'autorisation pour l'agence
        if (!isAuthorizedToManageTravel(userId, reservation.getIdVoyage())) {
            throw new AnnulationException("Vous n'êtes pas autorisé à annuler cette réservation", HttpStatus.FORBIDDEN);
        }

        if (reservation.getStatutReservation() == StatutReservation.ANNULER) {
            throw new AnnulationException("Cette réservation est déjà annulée", HttpStatus.CONFLICT);
        }

        // Récupération de l'historique
        Historique historique = historiqueRepository.findByIdReservation(reservation.getIdReservation())
                .orElseThrow(() -> new AnnulationException("L'historique associé à la réservation n'existe pas", HttpStatus.INTERNAL_SERVER_ERROR));

        List<Passager> passagersReservation = passagerRepository.findAllByIdReservation(reservation.getIdReservation());

        Voyage voyage = reservationCancelDTO.getVoyage();
        CancellationData cancellationData = getCancellationData(voyage.getIdVoyage());

        double tauxCompensation = AnnulationOperator.tauxCompensation(
                cancellationData.getClassVoyage(),
                cancellationData.getPolitiqueAnnulation(),
                voyage.getDateLimiteReservation(),
                voyage.getDateLimiteConfirmation(),
                now
        );

        // Si ce n'est qu'une simulation
        if (!reservationCancelDTO.isCanceled()) {
            return tauxCompensation * reservation.getMontantPaye();
        }

        // Suppression des passagers
        passagersReservation.forEach(passager ->
                passagerRepository.deleteById(passager.getIdPassager())
        );

        // Mise à jour de l'historique
        updateHistoriqueForAgencyCancellation(historique, reservation, voyage,
                reservationCancelDTO, tauxCompensation, now);

        // Création du coupon de compensation
        createCompensationCoupon(reservation, cancellationData.getLigneVoyage(),
                cancellationData.getPolitiqueAnnulation(), tauxCompensation, historique.getIdHistorique(), now);

        // Mise à jour de la réservation
        reservation.setStatutReservation(StatutReservation.ANNULER);

        //Envoyer notification d'annulation par l'agence
        if (reservationCancelDTO.isCanceled()) {
            try {
                notificationService.sendNotification(
                        NotificationEvent.builder()
                                .type(NotificationType.RESERVATION_CANCELLED)
                                .recipientType(RecipientType.USER)
                                .recipientId(reservation.getIdUser())
                                .variables(Map.of(
                                        "reservationId", reservation.getIdReservation().toString(),
                                        "voyageTitle", voyage.getTitre(),
                                        "cancelReason", reservationCancelDTO.getCauseAnnulation(),
                                        "compensationAmount", reservation.getMontantPaye() * (1 + tauxCompensation)
                                ))
                                .build()
                );
            } catch (Exception e) {
                log.warn("Erreur lors de l'envoi de la notification d'annulation par agence: {}", e.getMessage());
            }
        }

        reservationRepository.save(reservation);
        voyageRepository.save(voyage);

        log.info("Réservation {} annulée par l'agence avec succès", reservation.getIdReservation());
        return -1.0;

    }

    /**
     * Vérifie si un utilisateur est autorisé à gérer un voyage
     */
    private boolean isAuthorizedToManageTravel(UUID userId, UUID voyageId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AnnulationException("Utilisateur non trouvé", HttpStatus.NOT_FOUND));

            List<RoleType> userRoles = user.getRole();
            boolean hasRequiredRole = userRoles.contains(RoleType.AGENCE_VOYAGE) ||
                    userRoles.contains(RoleType.ORGANISATION);

            if (!hasRequiredRole) {
                return false;
            }

            // Vérifier que l'utilisateur est bien propriétaire de l'agence qui gère ce voyage
            LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyageId);
            if (ligneVoyage == null) {
                return false;
            }

            return ligneVoyage.getIdAgenceVoyage().equals(userId);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'autorisation: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Libère le véhicule associé à un voyage lors d'une annulation complète
     */
    private void libererVehicule(UUID voyageId) {
        try {
            LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyageId);
            if (ligneVoyage != null && ligneVoyage.getIdVehicule() != null) {
                // Le véhicule est maintenant disponible pour d'autres voyages
                // On pourrait ajouter ici une logique spécifique selon les besoins métier
                log.info("Véhicule {} libéré suite à l'annulation du voyage {}",
                        ligneVoyage.getIdVehicule(), voyageId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la libération du véhicule pour le voyage {}: {}",
                    voyageId, e.getMessage());
        }
    }

    private CancellationData getCancellationData(UUID voyageId) {
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new AnnulationException("Le voyage spécifié n'existe pas", HttpStatus.NOT_FOUND));

        LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyageId);
        if (ligneVoyage == null) {
            throw new AnnulationException("Ligne de voyage non trouvée", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage())
                .orElse(null);

        PolitiqueAnnulation politiqueAnnulation = politiqueAnnulationRepository
                .findByIdAgenceVoyage(ligneVoyage.getIdAgenceVoyage());

        return new CancellationData(voyage, ligneVoyage, classVoyage, politiqueAnnulation);
    }

    /**
     * Traite l'annulation effective d'une réservation
     */
    private double processReservationCancellation(Reservation reservation,
                                                  ReservationCancelDTO reservationCancelDTO, Historique historique,
                                                  CancellationData cancellationData, double tauxAnnulation, Date now) {

        double montantSubstitut = 0.0;
        double montantPaye = reservation.getMontantPaye();

        // Suppression des passagers et calcul du montant substitut
        for (UUID passagerId : reservationCancelDTO.getIdPassagers()) {
            passagerRepository.deleteById(passagerId);
            if (montantPaye >= cancellationData.getClassVoyage().getPrix()) {
                montantPaye -= cancellationData.getClassVoyage().getPrix();
                montantSubstitut += cancellationData.getClassVoyage().getPrix();
            } else if (montantPaye > 0) {
                montantSubstitut += montantPaye;
                montantPaye = 0;
            }
        }

        // Mise à jour de l'historique
        updateHistoriqueForUserCancellation(historique, reservation, cancellationData.getVoyage(),
                reservationCancelDTO, tauxAnnulation, now);

        // Création du coupon si nécessaire
        if (reservation.getMontantPaye() > 0) {
            createRefundCoupon(reservation, cancellationData.getLigneVoyage(),
                    cancellationData.getPolitiqueAnnulation(), montantSubstitut,
                    tauxAnnulation, historique.getIdHistorique(), now);
        }

        // Mise à jour de la réservation
        updateReservationAfterCancellation(reservation, reservationCancelDTO,
                montantPaye, cancellationData.getClassVoyage());

        //Envoyer notification d'annulation de réservation
        try {
            notificationService.sendNotification(
                    NotificationFactory.createReservationCancelledEvent(reservation)
            );
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de la notification d'annulation de réservation: {}", e.getMessage());
        }

        voyageRepository.save(cancellationData.getVoyage());
        historiqueRepository.save(historique);

        return -1.0;
    }

    /**
     * Met à jour l'historique pour une annulation par l'utilisateur
     */
    private void updateHistoriqueForUserCancellation(Historique historique, Reservation reservation,
                                                     Voyage voyage, ReservationCancelDTO reservationCancelDTO, double tauxAnnulation, Date now) {

        historique.setDateAnnulation(now);
        historique.setCauseAnnulation(reservationCancelDTO.getCauseAnnulation());
        historique.setTauxAnnulation(tauxAnnulation);
        historique.setOrigineAnnulation(reservationCancelDTO.getOrigineAnnulation());

        if (reservation.getStatutReservation() == StatutReservation.RESERVER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_USAGER_APRES_RESERVATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservationCancelDTO.getIdPassagers().length);
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservationCancelDTO.getIdPassagers().length);
        } else if (reservation.getStatutReservation() == StatutReservation.CONFIRMER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_USAGER_APRES_CONFIRMATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservationCancelDTO.getIdPassagers().length);
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservationCancelDTO.getIdPassagers().length);
            voyage.setNbrPlaceConfirm(voyage.getNbrPlaceConfirm() - reservationCancelDTO.getIdPassagers().length);
            voyage.setNbrPlaceRestante(voyage.getNbrPlaceRestante() + reservationCancelDTO.getIdPassagers().length);
        }
    }

    /**
     * Met à jour l'historique pour une annulation par l'agence
     */
    private void updateHistoriqueForAgencyCancellation(Historique historique, Reservation reservation,
                                                       Voyage voyage, ReservationCancelByAgenceDTO reservationCancelDTO,
                                                       double tauxCompensation, Date now) {

        historique.setDateAnnulation(now);
        historique.setCauseAnnulation(reservationCancelDTO.getCauseAnnulation());
        historique.setOrigineAnnulation(reservationCancelDTO.getOrigineAnnulation());
        historique.setCompensation(tauxCompensation);

        if (reservation.getStatutReservation() == StatutReservation.RESERVER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_AGENCE_APRES_RESERVATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservation.getNbrPassager());
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservation.getNbrPassager());
        } else if (reservation.getStatutReservation() == StatutReservation.CONFIRMER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_AGENCE_APRES_CONFIRMATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservation.getNbrPassager());
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservation.getNbrPassager());
            voyage.setNbrPlaceConfirm(voyage.getNbrPlaceConfirm() - reservation.getNbrPassager());
            voyage.setNbrPlaceRestante(voyage.getNbrPlaceRestante() + reservation.getNbrPassager());
        }

        historiqueRepository.save(historique);
    }

    /**
     * Crée un coupon de remboursement pour l'utilisateur
     */
    private void createRefundCoupon(Reservation reservation, LigneVoyage ligneVoyage,
                                    PolitiqueAnnulation politiqueAnnulation, double montantSubstitut,
                                    double tauxAnnulation, UUID historiqueId, Date now) {

        SoldeIndemnisation soldeIndemnisation = getOrCreateSoldeIndemnisation(
                reservation.getIdUser(), ligneVoyage.getIdAgenceVoyage());

        Coupon coupon = new Coupon();
        coupon.setIdCoupon(UUID.randomUUID());
        coupon.setDateDebut(now);

        Instant debutInstant = now.toInstant();
        Instant finDate = debutInstant.plus(politiqueAnnulation.getDureeCoupon());
        coupon.setDateFin(Date.from(finDate));
        coupon.setValeur(montantSubstitut * (1 - tauxAnnulation));
        coupon.setStatusCoupon(StatutCoupon.VALIDE);
        coupon.setIdHistorique(historiqueId);
        coupon.setIdSoldeIndemnisation(soldeIndemnisation.getIdSolde());

        soldeIndemnisation.setSolde(soldeIndemnisation.getSolde() + montantSubstitut * (1 - tauxAnnulation));

        couponRepository.save(coupon);
        soldeIndemnisationRepository.save(soldeIndemnisation);
    }

    /**
     * Crée un coupon de compensation pour l'utilisateur (annulation par agence)
     */
    private void createCompensationCoupon(Reservation reservation, LigneVoyage ligneVoyage,
                                          PolitiqueAnnulation politiqueAnnulation, double tauxCompensation,
                                          UUID historiqueId, Date now) {

        if (reservation.getMontantPaye() <= 0) {
            return;
        }

        SoldeIndemnisation soldeIndemnisation = getOrCreateSoldeIndemnisation(
                reservation.getIdUser(), ligneVoyage.getIdAgenceVoyage());

        Coupon coupon = new Coupon();
        coupon.setIdCoupon(UUID.randomUUID());
        coupon.setDateDebut(now);

        Instant debutInstant = now.toInstant();
        Instant finDate = debutInstant.plus(politiqueAnnulation.getDureeCoupon());
        coupon.setDateFin(Date.from(finDate));
        coupon.setValeur(reservation.getMontantPaye() * (1 + tauxCompensation));
        coupon.setStatusCoupon(StatutCoupon.VALIDE);
        coupon.setIdHistorique(historiqueId);
        coupon.setIdSoldeIndemnisation(soldeIndemnisation.getIdSolde());

        soldeIndemnisation.setSolde(soldeIndemnisation.getSolde() +
                reservation.getMontantPaye() * (1 + tauxCompensation));

        couponRepository.save(coupon);
        soldeIndemnisationRepository.save(soldeIndemnisation);
    }

    /**
     * Récupère ou crée un solde d'indemnisation
     */
    private SoldeIndemnisation getOrCreateSoldeIndemnisation(UUID userId, UUID agenceId) {
        return soldeIndemnisationRepository.findByIdUserAndIdAgenceVoyage(userId, agenceId)
                .orElseGet(() -> {
                    SoldeIndemnisation nouveau = new SoldeIndemnisation();
                    nouveau.setIdSolde(UUID.randomUUID());
                    nouveau.setIdUser(userId);
                    nouveau.setIdAgenceVoyage(agenceId);
                    nouveau.setSolde(0);
                    return nouveau;
                });
    }

    /**
     * Met à jour la réservation après annulation
     */
    private void updateReservationAfterCancellation(Reservation reservation,
                                                    ReservationCancelDTO reservationCancelDTO, double montantPaye, ClassVoyage classVoyage) {

        reservation.setMontantPaye(montantPaye);
        reservation.setNbrPassager(reservation.getNbrPassager() - reservationCancelDTO.getIdPassagers().length);
        reservation.setPrixTotal((reservation.getNbrPassager() - reservationCancelDTO.getIdPassagers().length) * classVoyage.getPrix());

        if (reservation.getNbrPassager() <= 0) {
            reservation.setStatutReservation(StatutReservation.ANNULER);
            reservationRepository.save(reservation);
        }
    }

    /**
     * Tâche planifiée pour vérifier les réservations expirées
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkReservation() {
        log.info("Début de la vérification des réservations expirées");

        List<StatutReservation> statutsAVerifier = List.of(
                StatutReservation.VALIDER,
                StatutReservation.RESERVER
        );

        List<Reservation> reservations = reservationRepository.findAllByStatutReservationIsIn(statutsAVerifier);
        Date now = new Date();

        for (Reservation reservation : reservations) {
            try {
                processExpiredReservation(reservation, now);
            } catch (Exception e) {
                log.error("Erreur lors du traitement de la réservation expirée {}: {}",
                        reservation.getIdReservation(), e.getMessage());
            }
        }

        log.info("Fin de la vérification des réservations expirées. {} réservations traitées",
                reservations.size());
    }

    /**
     * Traite une réservation expirée
     */
    private void processExpiredReservation(Reservation reservation, Date now) {
        Voyage voyage = voyageRepository.findById(reservation.getIdVoyage())
                .orElse(null);

        if (voyage == null || !now.after(voyage.getDateLimiteConfirmation())) {
            return;
        }

        log.info("Traitement de l'annulation automatique pour la réservation {}",
                reservation.getIdReservation());

        Historique historique = historiqueRepository.findByIdReservation(reservation.getIdReservation())
                .orElseThrow(() -> new RuntimeException("Historique non trouvé"));

        List<Passager> passagers = passagerRepository.findAllByIdReservation(reservation.getIdReservation());
        CancellationData cancellationData = getCancellationData(voyage.getIdVoyage());

        // Traitement automatique de l'annulation
        processAutomaticCancellation(reservation, historique, passagers, cancellationData, now);
    }

    /**
     * Traite une annulation automatique
     */
    private void processAutomaticCancellation(Reservation reservation, Historique historique,
                                              List<Passager> passagers, CancellationData cancellationData, Date now) {

        double montantSubstitut = calculateMontantSubstitut(reservation, passagers, cancellationData.getClassVoyage());

        double tauxAnnulation = AnnulationOperator.tauxannualtion(
                cancellationData.getClassVoyage(),
                cancellationData.getPolitiqueAnnulation(),
                cancellationData.getVoyage().getDateLimiteReservation(),
                cancellationData.getVoyage().getDateLimiteConfirmation(),
                now
        );

        // Suppression des passagers
        passagers.forEach(passager -> passagerRepository.deleteById(passager.getIdPassager()));

        // Mise à jour de l'historique
        updateHistoriqueForAutomaticCancellation(historique, reservation,
                cancellationData.getVoyage(), tauxAnnulation, now);

        // Création du coupon si nécessaire
        if (reservation.getMontantPaye() > 0) {
            createRefundCoupon(reservation, cancellationData.getLigneVoyage(),
                    cancellationData.getPolitiqueAnnulation(), montantSubstitut,
                    tauxAnnulation, historique.getIdHistorique(), now);
        }

        // Mise à jour de la réservation
        updateReservationForAutomaticCancellation(reservation, passagers, cancellationData.getClassVoyage());

        //Envoyer notification d'annulation automatique
        try {
            notificationService.sendNotification(
                    NotificationEvent.builder()
                            .type(NotificationType.RESERVATION_EXPIRED)
                            .recipientType(RecipientType.USER)
                            .recipientId(reservation.getIdUser())
                            .variables(Map.of(
                                    "reservationId", reservation.getIdReservation().toString(),
                                    "voyageTitle", cancellationData.getVoyage().getTitre(),
                                    "reason", "Délai de confirmation dépassé"
                            ))
                            .build()
            );
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi de la notification d'annulation automatique: {}", e.getMessage());
        }

        voyageRepository.save(cancellationData.getVoyage());
        historiqueRepository.save(historique);
    }

    /**
     * Met à jour l'historique pour une annulation automatique
     */
    private void updateHistoriqueForAutomaticCancellation(Historique historique, Reservation reservation,
                                                          Voyage voyage, double tauxAnnulation, Date now) {

        historique.setDateAnnulation(now);
        historique.setCauseAnnulation("Annulation automatique après passage de la date limite de confirmation");
        historique.setTauxAnnulation(tauxAnnulation);
        historique.setOrigineAnnulation("Le client n'a pas confirmé sa réservation à temps");

        if (reservation.getStatutReservation() == StatutReservation.RESERVER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_USAGER_APRES_RESERVATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservation.getNbrPassager());
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservation.getNbrPassager());
        } else if (reservation.getStatutReservation() == StatutReservation.CONFIRMER) {
            historique.setStatusHistorique(StatutHistorique.ANNULER_PAR_USAGER_APRES_CONFIRMATION);
            voyage.setNbrPlaceReserve(voyage.getNbrPlaceReserve() - reservation.getNbrPassager());
            voyage.setNbrPlaceReservable(voyage.getNbrPlaceReservable() + reservation.getNbrPassager());
            voyage.setNbrPlaceConfirm(voyage.getNbrPlaceConfirm() - reservation.getNbrPassager());
            voyage.setNbrPlaceRestante(voyage.getNbrPlaceRestante() + reservation.getNbrPassager());
        }
    }

    /**
     * Met à jour la réservation pour une annulation automatique
     */
    private void updateReservationForAutomaticCancellation(Reservation reservation,
                                                           List<Passager> passagers, ClassVoyage classVoyage) {

        double montantPaye = reservation.getMontantPaye();

        // Calcul du nouveau montant payé après suppression des passagers
        for (Passager passager : passagers) {
            if (montantPaye >= classVoyage.getPrix()) {
                montantPaye -= classVoyage.getPrix();
            } else {
                montantPaye = 0;
                break;
            }
        }

        reservation.setMontantPaye(montantPaye);
        reservation.setNbrPassager(reservation.getNbrPassager() - passagers.size());
        reservation.setPrixTotal((reservation.getNbrPassager() - passagers.size()) * classVoyage.getPrix());

        if (reservation.getNbrPassager() <= 0) {
            //reservationRepository.delete(reservation);
            reservation.setStatutReservation(StatutReservation.ANNULER);
        }
        reservationRepository.save(reservation);
    }

    /**
     * Calcule le montant substitut pour une annulation automatique
     */
    private double calculateMontantSubstitut(Reservation reservation, List<Passager> passagers, ClassVoyage classVoyage) {
        double montantSubstitut = 0.0;
        double montantPaye = reservation.getMontantPaye();

        for (int i = 0; i < passagers.size(); i++) {
            if (montantPaye >= classVoyage.getPrix()) {
                montantPaye -= classVoyage.getPrix();
                montantSubstitut += classVoyage.getPrix();
            } else if (montantPaye > 0) {
                montantSubstitut += montantPaye;
                montantPaye = 0;
            }
        }

        return montantSubstitut;
    }

}

package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingDetailDto;
import com.yrog.fermettetroistilleuls.dto.GiteBookingDetailDto;
import com.yrog.fermettetroistilleuls.entity.ActivityBooking;
import com.yrog.fermettetroistilleuls.entity.BookingStatus;
import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier de l'espace admin.
 * Permet de consulter et traiter les demandes de réservation.
 */
@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final GiteBookingRepository giteBookingRepository;
    private final ActivityBookingRepository activityBookingRepository;

    public AdminService(GiteBookingRepository giteBookingRepository,
                        ActivityBookingRepository activityBookingRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.activityBookingRepository = activityBookingRepository;
    }

    /**
     * Retourne toutes les réservations de gîtes
     * triées par statut (PENDING en premier)
     * puis par date de création décroissante.
     *
     * @return liste de GiteBookingDetailDto
     */
    @Transactional(readOnly = true)
    public List<GiteBookingDetailDto> findAllGiteBookings() {
        log.info("Récupération de toutes les réservations de gîtes");
        return giteBookingRepository.findAllByOrderByStatusAscCreatedAtDesc()
                .stream()
                .map(booking -> new GiteBookingDetailDto(
                        booking.getId(),
                        booking.getFirstName(),
                        booking.getLastName(),
                        booking.getEmail(),
                        booking.getPhone(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getNbGuests(),
                        booking.getMessage(),
                        booking.getStatus(),
                        booking.getCreatedAt(),
                        booking.getGite().getName()
                ))
                .toList();
    }

    /**
     * Retourne toutes les réservations d'activités
     * triées par statut (PENDING en premier)
     * puis par date de création décroissante.
     *
     * @return liste de ActivityBookingDetailDto
     */
    @Transactional(readOnly = true)
    public List<ActivityBookingDetailDto> findAllActivityBookings() {
        log.info("Récupération de toutes les réservations d'activités");
        return activityBookingRepository.findAllByOrderByStatusAscCreatedAtDesc()
                .stream()
                .map(booking -> new ActivityBookingDetailDto(
                        booking.getId(),
                        booking.getFirstName(),
                        booking.getLastName(),
                        booking.getEmail(),
                        booking.getPhone(),
                        booking.getNbGuests(),
                        booking.getMessage(),
                        booking.getStatus(),
                        booking.getCreatedAt(),
                        booking.getActivity().getName(),
                        booking.getActivity().getDate(),
                        booking.getActivity().getTime()
                ))
                .toList();
    }

    /**
     * Accepte une demande de réservation de gîte.
     * Change le statut à ACCEPTED.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void acceptGiteBooking(Long id) {
        GiteBooking booking = giteBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation gîte introuvable : " + id));
        booking.setStatus(BookingStatus.ACCEPTED);
        giteBookingRepository.save(booking);
        log.info("Réservation gîte {} acceptée", id);
    }

    /**
     * Refuse une demande de réservation de gîte.
     * Change le statut à REJECTED.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void rejectGiteBooking(Long id) {
        GiteBooking booking = giteBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation gîte introuvable : " + id));
        booking.setStatus(BookingStatus.REJECTED);
        giteBookingRepository.save(booking);
        log.info("Réservation gîte {} refusée", id);
    }

    /**
     * Accepte une demande de réservation d'activité.
     * Change le statut à ACCEPTED.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void acceptActivityBooking(Long id) {
        ActivityBooking booking = activityBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation activité introuvable : " + id));
        booking.setStatus(BookingStatus.ACCEPTED);
        activityBookingRepository.save(booking);
        log.info("Réservation activité {} acceptée", id);
    }

    /**
     * Refuse une demande de réservation d'activité.
     * Change le statut à REJECTED.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void rejectActivityBooking(Long id) {
        ActivityBooking booking = activityBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation activité introuvable : " + id));
        booking.setStatus(BookingStatus.REJECTED);
        activityBookingRepository.save(booking);
        log.info("Réservation activité {} refusée", id);
    }
}

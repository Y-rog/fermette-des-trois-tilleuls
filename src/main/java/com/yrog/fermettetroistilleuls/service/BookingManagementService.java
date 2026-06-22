package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingDetailDto;
import com.yrog.fermettetroistilleuls.dto.GiteBookingDetailDto;
import com.yrog.fermettetroistilleuls.entity.*;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service gérant le cycle de vie des réservations
 * depuis l'espace admin : consultation, acceptation,
 * refus et remise en attente.
 */
@Service
public class BookingManagementService {

    private static final Logger log = LoggerFactory.getLogger(BookingManagementService.class);

    private final GiteBookingRepository giteBookingRepository;
    private final ActivityBookingRepository activityBookingRepository;
    private final MailService mailService;
    private final GiteRepository giteRepository;
    private final GiteAvailabilityRepository giteAvailabilityRepository;

    public BookingManagementService(GiteBookingRepository giteBookingRepository,
                                    ActivityBookingRepository activityBookingRepository,
                                    MailService mailService,
                                    GiteRepository giteRepository,
                                    GiteAvailabilityRepository giteAvailabilityRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.activityBookingRepository = activityBookingRepository;
        this.mailService = mailService;
        this.giteRepository = giteRepository;
        this.giteAvailabilityRepository = giteAvailabilityRepository;
    }

    /**
     * Retourne toutes les réservations de gîtes triées par statut
     * (PENDING en premier) puis par date d'arrivée croissante.
     *
     * @return liste de GiteBookingDetailDto
     */
    @Transactional(readOnly = true)
    public List<GiteBookingDetailDto> findAllGiteBookings() {
        log.info("Récupération de toutes les réservations de gîtes");
        return giteBookingRepository.findAllByOrderByStatusAscCheckInAsc()
                .stream()
                .filter(booking -> !booking.getCheckOut().isBefore(LocalDate.now()))
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
     * Retourne toutes les réservations d'activités triées par statut
     * (PENDING en premier) puis par date de création décroissante.
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
     * Retourne le détail d'une réservation de gîte par son id.
     *
     * @param id identifiant de la réservation
     * @return GiteBookingDetailDto
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional(readOnly = true)
    public GiteBookingDetailDto findGiteBookingById(Long id) {
        return giteBookingRepository.findById(id)
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation gîte introuvable : " + id));
    }

    /**
     * Retourne le détail d'une réservation d'activité par son id.
     *
     * @param id identifiant de la réservation
     * @return ActivityBookingDetailDto
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional(readOnly = true)
    public ActivityBookingDetailDto findActivityBookingById(Long id) {
        return activityBookingRepository.findById(id)
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation activité introuvable : " + id));
    }

    /**
     * Accepte une demande de réservation de gîte.
     * Change le statut à ACCEPTED, marque les dates comme RESERVED
     * et envoie un email de confirmation avec les détails du séjour.
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

        markDatesAsReserved(booking.getGite().getId(), booking.getCheckIn(), booking.getCheckOut());

        mailService.sendBookingConfirmation(
                booking.getEmail(),
                booking.getFirstName(),
                booking.getGite().getName(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );
        log.info("Réservation gîte {} acceptée", id);
    }

    /**
     * Marque toutes les dates entre checkIn et checkOut
     * comme RESERVED pour ce gîte.
     *
     * @param giteId   identifiant du gîte
     * @param checkIn  date d'arrivée
     * @param checkOut date de départ
     */
    private void markDatesAsReserved(Long giteId, LocalDate checkIn, LocalDate checkOut) {
        Gite gite = giteRepository.findById(giteId)
                .orElseThrow(() -> new ResourceNotFoundException("Gîte introuvable"));

        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            GiteAvailability availability = giteAvailabilityRepository
                    .findByGiteIdAndDate(giteId, current)
                    .orElse(null);

            if (availability != null) {
                availability.setStatus(AvailabilityStatus.RESERVED);
                giteAvailabilityRepository.save(availability);
            } else {
                giteAvailabilityRepository.save(
                        GiteAvailability.builder()
                                .gite(gite)
                                .date(current)
                                .status(AvailabilityStatus.RESERVED)
                                .build()
                );
            }
            current = current.plusDays(1);
        }
        log.info("Dates marquées RESERVED pour gîte {} du {} au {}", giteId, checkIn, checkOut);
    }

    /**
     * Libère toutes les dates entre checkIn et checkOut
     * en les remettant à AVAILABLE.
     *
     * @param giteId   identifiant du gîte
     * @param checkIn  date d'arrivée
     * @param checkOut date de départ
     */
    private void releaseDates(Long giteId, LocalDate checkIn, LocalDate checkOut) {
        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            giteAvailabilityRepository
                    .findByGiteIdAndDate(giteId, current)
                    .ifPresent(availability -> {
                        if (availability.getStatus() == AvailabilityStatus.RESERVED) {
                            availability.setStatus(AvailabilityStatus.AVAILABLE);
                            giteAvailabilityRepository.save(availability);
                        }
                    });
            current = current.plusDays(1);
        }
        log.info("Dates libérées pour gîte {} du {} au {}", giteId, checkIn, checkOut);
    }

    /**
     * Refuse une demande de réservation de gîte.
     * Libère les dates si la réservation était acceptée
     * et envoie un email de refus avec les détails du séjour.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void rejectGiteBooking(Long id) {
        GiteBooking booking = giteBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation gîte introuvable : " + id));

        if (booking.getStatus() == BookingStatus.ACCEPTED) {
            releaseDates(booking.getGite().getId(), booking.getCheckIn(), booking.getCheckOut());
        }

        booking.setStatus(BookingStatus.REJECTED);
        giteBookingRepository.save(booking);

        mailService.sendBookingRejection(
                booking.getEmail(),
                booking.getFirstName(),
                booking.getGite().getName(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );
        log.info("Réservation gîte {} refusée", id);
    }

    /**
     * Remet une réservation de gîte en attente.
     * Libère les dates si la réservation était acceptée.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void pendingGiteBooking(Long id) {
        GiteBooking booking = giteBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation gîte introuvable : " + id));

        if (booking.getStatus() == BookingStatus.ACCEPTED) {
            releaseDates(booking.getGite().getId(), booking.getCheckIn(), booking.getCheckOut());
        }

        booking.setStatus(BookingStatus.PENDING);
        giteBookingRepository.save(booking);
        log.info("Réservation gîte {} remise en attente", id);
    }

    /**
     * Accepte une demande de réservation d'activité.
     * Change le statut à ACCEPTED et envoie un email de confirmation
     * avec les détails de l'activité.
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

        mailService.sendBookingConfirmation(
                booking.getEmail(),
                booking.getFirstName(),
                booking.getActivity().getName(),
                booking.getActivity().getDate(),
                booking.getActivity().getDate()
        );
        log.info("Réservation activité {} acceptée", id);
    }

    /**
     * Refuse une demande de réservation d'activité.
     * Change le statut à REJECTED et envoie un email de refus
     * avec les détails de l'activité.
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

        mailService.sendBookingRejection(
                booking.getEmail(),
                booking.getFirstName(),
                booking.getActivity().getName(),
                booking.getActivity().getDate(),
                booking.getActivity().getDate()
        );
        log.info("Réservation activité {} refusée", id);
    }

    /**
     * Remet une réservation d'activité en attente.
     * Change le statut à PENDING.
     *
     * @param id identifiant de la réservation
     * @throws ResourceNotFoundException si la réservation n'existe pas
     */
    @Transactional
    public void pendingActivityBooking(Long id) {
        ActivityBooking booking = activityBookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Réservation activité introuvable : " + id));
        booking.setStatus(BookingStatus.PENDING);
        activityBookingRepository.save(booking);
        log.info("Réservation activité {} remise en attente", id);
    }
}
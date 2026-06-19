package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingDetailDto;
import com.yrog.fermettetroistilleuls.dto.GiteBookingDetailDto;
import com.yrog.fermettetroistilleuls.entity.ActivityBooking;
import com.yrog.fermettetroistilleuls.entity.ActivityBookingArchive;
import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import com.yrog.fermettetroistilleuls.entity.GiteBookingArchive;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingArchiveRepository;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingArchiveRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service gérant l'archivage des réservations terminées.
 * Déplace les réservations dont le séjour/l'activité est passé(e)
 * vers des tables d'archive, pour garder les tables principales
 * légères et rapides.
 */
@Service
public class ArchiveService {

    private static final Logger log = LoggerFactory.getLogger(ArchiveService.class);

    private final GiteBookingRepository giteBookingRepository;
    private final GiteBookingArchiveRepository giteBookingArchiveRepository;
    private final ActivityBookingRepository activityBookingRepository;
    private final ActivityBookingArchiveRepository activityBookingArchiveRepository;

    public ArchiveService(GiteBookingRepository giteBookingRepository,
                          GiteBookingArchiveRepository giteBookingArchiveRepository,
                          ActivityBookingRepository activityBookingRepository,
                          ActivityBookingArchiveRepository activityBookingArchiveRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.giteBookingArchiveRepository = giteBookingArchiveRepository;
        this.activityBookingRepository = activityBookingRepository;
        this.activityBookingArchiveRepository = activityBookingArchiveRepository;
    }

    /**
     * Archive toutes les réservations de gîtes dont le séjour
     * est terminé (checkOut antérieur à aujourd'hui).
     * Copie chaque réservation dans la table d'archive
     * puis la supprime de la table active.
     *
     * @return le nombre de réservations archivées
     */
    @Transactional
    public int archiveOldGiteBookings() {
        List<GiteBooking> oldBookings = giteBookingRepository.findAll()
                .stream()
                .filter(b -> b.getCheckOut().isBefore(LocalDate.now()))
                .toList();

        for (GiteBooking booking : oldBookings) {
            GiteBookingArchive archive = GiteBookingArchive.builder()
                    .giteName(booking.getGite().getName())
                    .firstName(booking.getFirstName())
                    .lastName(booking.getLastName())
                    .email(booking.getEmail())
                    .phone(booking.getPhone())
                    .checkIn(booking.getCheckIn())
                    .checkOut(booking.getCheckOut())
                    .nbGuests(booking.getNbGuests())
                    .message(booking.getMessage())
                    .status(booking.getStatus())
                    .createdAt(booking.getCreatedAt())
                    .archivedAt(LocalDateTime.now())
                    .build();

            giteBookingArchiveRepository.save(archive);
            giteBookingRepository.delete(booking);
        }

        log.info("{} réservations de gîtes archivées", oldBookings.size());
        return oldBookings.size();
    }

    /**
     * Archive toutes les réservations d'activités dont la date
     * est passée. Copie chaque réservation dans la table d'archive
     * puis la supprime de la table active.
     *
     * @return le nombre de réservations archivées
     */
    @Transactional
    public int archiveOldActivityBookings() {
        List<ActivityBooking> oldBookings = activityBookingRepository.findAll()
                .stream()
                .filter(b -> b.getActivity().getDate().isBefore(LocalDate.now()))
                .toList();

        for (ActivityBooking booking : oldBookings) {
            ActivityBookingArchive archive = ActivityBookingArchive.builder()
                    .activityName(booking.getActivity().getName())
                    .activityDate(booking.getActivity().getDate())
                    .activityTime(booking.getActivity().getTime())
                    .firstName(booking.getFirstName())
                    .lastName(booking.getLastName())
                    .email(booking.getEmail())
                    .phone(booking.getPhone())
                    .nbGuests(booking.getNbGuests())
                    .message(booking.getMessage())
                    .status(booking.getStatus())
                    .createdAt(booking.getCreatedAt())
                    .archivedAt(LocalDateTime.now())
                    .build();

            activityBookingArchiveRepository.save(archive);
            activityBookingRepository.delete(booking);
        }

        log.info("{} réservations d'activités archivées", oldBookings.size());
        return oldBookings.size();
    }

    /**
     * Retourne l'historique complet des réservations de gîtes archivées,
     * triées par date d'arrivée décroissante.
     *
     * @return liste de GiteBookingDetailDto
     */
    @Transactional(readOnly = true)
    public List<GiteBookingDetailDto> findGiteBookingsHistory() {
        log.info("Récupération de l'historique des réservations de gîtes");
        return giteBookingArchiveRepository.findAllByOrderByCheckInDesc()
                .stream()
                .map(a -> new GiteBookingDetailDto(
                        a.getId(),
                        a.getFirstName(),
                        a.getLastName(),
                        a.getEmail(),
                        a.getPhone(),
                        a.getCheckIn(),
                        a.getCheckOut(),
                        a.getNbGuests(),
                        a.getMessage(),
                        a.getStatus(),
                        a.getCreatedAt(),
                        a.getGiteName()
                ))
                .toList();
    }

    /**
     * Retourne l'historique complet des réservations d'activités archivées,
     * triées par date d'activité décroissante.
     *
     * @return liste de ActivityBookingDetailDto
     */
    @Transactional(readOnly = true)
    public List<ActivityBookingDetailDto> findActivityBookingsHistory() {
        log.info("Récupération de l'historique des réservations d'activités");
        return activityBookingArchiveRepository.findAllByOrderByActivityDateDesc()
                .stream()
                .map(a -> new ActivityBookingDetailDto(
                        a.getId(),
                        a.getFirstName(),
                        a.getLastName(),
                        a.getEmail(),
                        a.getPhone(),
                        a.getNbGuests(),
                        a.getMessage(),
                        a.getStatus(),
                        a.getCreatedAt(),
                        a.getActivityName(),
                        a.getActivityDate(),
                        a.getActivityTime()
                ))
                .toList();
    }
}

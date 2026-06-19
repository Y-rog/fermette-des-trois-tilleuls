package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityDto;
import com.yrog.fermettetroistilleuls.dto.ActivityForm;
import com.yrog.fermettetroistilleuls.entity.Activity;
import com.yrog.fermettetroistilleuls.entity.BookingStatus;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingRepository;
import com.yrog.fermettetroistilleuls.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des activités.
 * Retourne toujours des DTOs — jamais des entités JPA directement.
 */
@Service
public class ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;
    private final ActivityBookingRepository activityBookingRepository;

    public ActivityService(ActivityRepository activityRepository, ActivityBookingRepository activityBookingRepository) {
        this.activityRepository = activityRepository;
        this.activityBookingRepository = activityBookingRepository;
    }

    /**
     * Retourne la liste de toutes les activités.
     *
     * @return liste de ActivityDto
     */
    @Transactional(readOnly = true)
    public List<ActivityDto> findAll() {
        log.info("Récupération de la liste des activités");
        return activityRepository.findAll()
                .stream()
                .map(activity -> new ActivityDto(
                        activity.getId(),
                        activity.getName(),
                        activity.getDescription(),
                        activity.getDate(),
                        activity.getTime()
                ))
                .toList();
    }

    /**
     * Retourne une activité par son identifiant.
     *
     * @param id identifiant de l'activité
     * @return ActivityDto correspondant
     * @throws ResourceNotFoundException si l'activité n'existe pas
     */
    @Transactional(readOnly = true)
    public ActivityDto findById(Long id) {
        log.info("Récupération de l'activité id={}", id);
        return activityRepository.findById(id)
                .map(activity -> new ActivityDto(
                        activity.getId(),
                        activity.getName(),
                        activity.getDescription(),
                        activity.getDate(),
                        activity.getTime()
                ))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable : " + id)
                );
    }

    /**
     * Crée une nouvelle activité.
     *
     * @param form formulaire rempli par l'admin
     */
    @Transactional
    public void create(ActivityForm form) {
        Activity activity = Activity.builder()
                .name(form.getName())
                .description(form.getDescription())
                .date(form.getDate())
                .time(form.getTime())
                .active(true)
                .build();

        activityRepository.save(activity);
        log.info("Nouvelle activité créée : {}", form.getName());
    }

    /**
     * Met à jour une activité existante.
     *
     * @param id   identifiant de l'activité
     * @param form formulaire rempli par l'admin
     * @throws ResourceNotFoundException si l'activité n'existe pas
     */
    @Transactional
    public void update(Long id, ActivityForm form) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable : " + id));

        activity.setName(form.getName());
        activity.setDescription(form.getDescription());
        activity.setDate(form.getDate());
        activity.setTime(form.getTime());

        activityRepository.save(activity);
        log.info("Activité {} mise à jour", id);
    }

    /**
     * Supprime une activité.
     * Refuse la suppression si des réservations PENDING ou ACCEPTED
     * sont en cours pour cette activité, afin d'éviter de perdre
     * l'historique d'une réservation active.
     *
     * @param id identifiant de l'activité
     * @throws ResourceNotFoundException si l'activité n'existe pas
     * @throws IllegalStateException     si des réservations sont en cours
     */
    @Transactional
    public void delete(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Activité introuvable : " + id);
        }

        boolean hasActiveBookings = activityBookingRepository
                .existsByActivityIdAndStatusIn(id, List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED));

        if (hasActiveBookings) {
            throw new IllegalStateException(
                    "Impossible de supprimer cette activité : des réservations sont en cours");
        }

        activityRepository.deleteById(id);
        log.info("Activité {} supprimée", id);
    }
}

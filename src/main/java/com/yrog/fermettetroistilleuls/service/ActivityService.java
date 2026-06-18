package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingForm;
import com.yrog.fermettetroistilleuls.dto.ActivityDto;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityRepository;
import jakarta.validation.Valid;
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

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
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

}

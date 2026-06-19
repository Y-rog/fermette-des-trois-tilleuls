package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteCalendarDto;
import com.yrog.fermettetroistilleuls.dto.MultiGiteCalendarDto;
import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;
import com.yrog.fermettetroistilleuls.entity.GiteAvailability;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service gérant la logique des calendriers
 * de disponibilités pour les gîtes et activités.
 */
@Service
public class CalendarService {

    private static final Logger log = LoggerFactory.getLogger(CalendarService.class);

    private final GiteAvailabilityRepository giteAvailabilityRepository;
    private final GiteRepository giteRepository;

    public CalendarService(GiteAvailabilityRepository giteAvailabilityRepository, GiteRepository giteRepository) {
        this.giteAvailabilityRepository = giteAvailabilityRepository;
        this.giteRepository = giteRepository;
    }

    /**
     * Construit le calendrier mensuel des disponibilités
     * pour un gîte et un mois donné.
     *
     * @param giteId identifiant du gîte
     * @param year   année
     * @param month  mois (1-12)
     * @return GiteCalendarDto
     */
    @Transactional(readOnly = true)
    public GiteCalendarDto buildGiteCalendar(Long giteId, int year, int month) {
        log.info("Construction du calendrier gîte id={} {}/{}", giteId, month, year);

        // 1. On calcule le 1er jour du mois demandé
        // Exemple : LocalDate.of(2026, 7, 1) → 1er juillet 2026
        LocalDate firstDay = LocalDate.of(year, month, 1);

        // 2. Combien de jours dans ce mois ?
        int daysInMonth = firstDay.lengthOfMonth();

        // 3. Sur quel jour de la semaine tombe le 1er ?
        // getDayOfWeek() retourne MONDAY, TUESDAY...
        // .getValue() le convertit en nombre : 1=lundi, 2=mardi... 7=dimanche
        // Si le 1er juillet 2026 est un mercredi → firstDayOfWeek = 3
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue();

        // 4. On calcule le dernier jour du mois
        // 31 juillet 2026
        LocalDate lastDay = firstDay.withDayOfMonth(daysInMonth);

        // 5. On va chercher en BDD toutes les dispos
        //    ENTRE le 1er et le dernier jour du mois
        // Résultat : {1: AVAILABLE, 2: AVAILABLE, 3: RESERVED, ...}
        Map<Integer, AvailabilityStatus> availMap = giteAvailabilityRepository.findByGiteIdOrderByDateAsc(giteId)
                        .stream()
                        .filter(a -> !a.getDate().isBefore(firstDay)
                                && !a.getDate().isAfter(lastDay))
                        .collect(Collectors.toMap(
                                a -> a.getDate().getDayOfMonth(), // ← clé = le numéro du jour
                                GiteAvailability::getStatus
                        ));

        // Nom du mois en français avec majuscule
        String monthName = firstDay.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.FRENCH);
        monthName = monthName.substring(0, 1).toUpperCase()
                + monthName.substring(1);

        return new GiteCalendarDto(
                year, month, monthName,
                firstDayOfWeek, daysInMonth, availMap
        );
    }

    /**
     * Construit le calendrier multi-gîtes pour le dashboard admin.
     * Permet de visualiser l'occupation de tous les gîtes
     * en un coup d'œil pour un mois donné.
     *
     * @param year  année du calendrier
     * @param month mois du calendrier (1-12)
     * @return MultiGiteCalendarDto prêt pour Thymeleaf
     */
    @Transactional(readOnly = true)
    public MultiGiteCalendarDto buildMultiGiteCalendar(int year, int month) {
        log.info("Construction du calendrier multi-gîtes {}/{}", month, year);

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        LocalDate lastDay = firstDay.withDayOfMonth(daysInMonth);

        String monthName = firstDay.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.FRENCH);
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);

        List<MultiGiteCalendarDto.GiteCalendarRowDto> rows = giteRepository.findAll()
                .stream()
                .map(gite -> {

                    Map<Integer, AvailabilityStatus> availMap =
                            giteAvailabilityRepository.findByGiteIdOrderByDateAsc(gite.getId())
                                    .stream()
                                    .filter(a -> !a.getDate().isBefore(firstDay)
                                            && !a.getDate().isAfter(lastDay))
                                    .collect(Collectors.toMap(
                                            a -> a.getDate().getDayOfMonth(),
                                            GiteAvailability::getStatus
                                    ));

                    return new MultiGiteCalendarDto.GiteCalendarRowDto(
                            gite.getId(),
                            gite.getName(),
                            availMap
                    );
                })
                .toList();

        List<String> dayLetters = new java.util.ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            String letter = date.getDayOfWeek()
                    .getDisplayName(TextStyle.NARROW, Locale.FRENCH);
            dayLetters.add(letter);
        }

        return new MultiGiteCalendarDto(year, month, monthName, daysInMonth, dayLetters, rows);

    }
}

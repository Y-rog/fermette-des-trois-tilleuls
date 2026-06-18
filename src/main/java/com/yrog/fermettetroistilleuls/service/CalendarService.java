package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteCalendarDto;
import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;
import com.yrog.fermettetroistilleuls.entity.GiteAvailability;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
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

    public CalendarService(GiteAvailabilityRepository giteAvailabilityRepository) {
        this.giteAvailabilityRepository = giteAvailabilityRepository;
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

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue();
        LocalDate lastDay = firstDay.withDayOfMonth(daysInMonth);

        // Récupérer les dispos du mois
        Map<Integer, AvailabilityStatus> availMap =
                giteAvailabilityRepository.findByGiteIdOrderByDateAsc(giteId)
                        .stream()
                        .filter(a -> !a.getDate().isBefore(firstDay)
                                && !a.getDate().isAfter(lastDay))
                        .collect(Collectors.toMap(
                                a -> a.getDate().getDayOfMonth(), // ← clé = jour du mois
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
}

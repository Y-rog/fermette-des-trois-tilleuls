package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityDto;
import com.yrog.fermettetroistilleuls.entity.Activity;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    private Activity buildActivity() {
        return Activity.builder()
                .id(1L)
                .name("Balade en alpagas")
                .description("Une heure de balade")
                .date(LocalDate.of(2026, 7, 15))
                .time(LocalTime.of(10, 0))
                .active(true)
                .build();
    }

    @Test
    void findAll_shouldReturnListActivityDtos() {
        when(activityRepository.findAll()).thenReturn(List.of(buildActivity()));

        List<ActivityDto> result = activityService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Balade en alpagas");
        assertThat(result.get(0).date()).isEqualTo(LocalDate.of(2026, 7, 15));

    }

    @Test
    void findById_shouldReturnActivityDto_whenIdExists() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(buildActivity()));

        ActivityDto result = activityService.findById(1L);

        assertThat(result.name()).isEqualTo("Balade en alpagas");
        assertThat(result.date()).isEqualTo(LocalDate.of(2026, 7, 15));

    }

    @Test
    void findById_shouldThrowException_whenIdNotFound() {
        when(activityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

    }


}

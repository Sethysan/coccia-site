package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.WeeklyOfferingResponse;
import com.cocciahouse.api.mapper.WeeklyOfferingMapper;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.WeeklyOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WeeklyOfferingService {

    private final WeeklyOfferingRepository weeklyOfferingRepository;
    private final WeeklyOfferingMapper weeklyOfferingMapper;

    public WeeklyOfferingService(
            WeeklyOfferingRepository weeklyOfferingRepository,
            WeeklyOfferingMapper weeklyOfferingMapper
    ) {
        this.weeklyOfferingRepository = weeklyOfferingRepository;
        this.weeklyOfferingMapper = weeklyOfferingMapper;
    }

    @Transactional(readOnly = true)
    public Optional<WeeklyOfferingResponse> getCurrentOffering() {

        LocalDate today = LocalDate.now();

        return weeklyOfferingRepository
                .findFirstByStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                        List.of(
                                WeeklyOfferingStatus.SCHEDULED,
                                WeeklyOfferingStatus.PUBLISHED
                        ),
                        today,
                        today
                )
                .map(weeklyOfferingMapper::toResponse);
    }
}
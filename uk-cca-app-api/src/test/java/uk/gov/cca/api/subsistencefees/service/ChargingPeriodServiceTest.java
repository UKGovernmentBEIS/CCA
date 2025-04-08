package uk.gov.cca.api.subsistencefees.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.ChargingPeriod;
import uk.gov.cca.api.subsistencefees.repository.ChargingPeriodRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChargingPeriodServiceTest {

    @InjectMocks
    private ChargingPeriodService chargingPeriodService;

    @Mock
    private ChargingPeriodRepository chargingPeriodRepository;

    @Test
    void getChargingYear() {
        LocalDate currentDate = LocalDate.of(2025, 5, 25);

        ChargingPeriod Y2025 = ChargingPeriod.builder()
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2026, 3, 31))
                .chargingYear(Year.of(2025))
                .build();

        when(chargingPeriodRepository.findChargingYear(currentDate))
                .thenReturn(Optional.of(Y2025.getChargingYear()));

        Year result = chargingPeriodService.getChargingYear(currentDate);

        assertThat(result).isEqualTo(Year.of(2025));
    }

    @Test
    void getChargingYear_throws_exception() {
        LocalDate currentDate = LocalDate.of(2025, 1, 1);

        when(chargingPeriodRepository.findChargingYear(currentDate))
                .thenReturn(Optional.empty());

        BusinessException thrown = assertThrows(
                BusinessException.class,
                () -> chargingPeriodService.getChargingYear(currentDate),
                "Expected to return a ChargingPeriod, but it didn't"
        );


        verify(chargingPeriodRepository, times(1)).findChargingYear(currentDate);
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, thrown.getErrorCode());
    }
}

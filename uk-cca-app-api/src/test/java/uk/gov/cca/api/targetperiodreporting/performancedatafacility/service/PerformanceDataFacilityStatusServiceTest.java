package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityStatusServiceTest {

    @InjectMocks
    private PerformanceDataFacilityStatusService service;

    @Mock
    private PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;

    @Mock
    private PerformanceDataFacilityRepository performanceDataFacilityRepository;

    @Mock
    private PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void submitPerformanceData_new() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityContainer data = PerformanceDataFacilityContainer.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .baselineVariableEnergy(BigDecimal.TEN)
                        .build())
                .build();
        final PerformanceDataFacility performanceData = PerformanceDataFacility.builder()
                .data(data)
                .targetPeriodType(targetPeriodType)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .build();

        final TargetPeriod targetPeriod = TargetPeriod.builder().businessId(targetPeriodType).build();
        final PerformanceDataFacilityEntity entity = PerformanceDataFacilityEntity.builder()
                .data(data)
                .targetPeriod(targetPeriod)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .reportVersion(1)
                .build();
        final PerformanceDataFacilityStatus performanceDataFacilityStatus = PerformanceDataFacilityStatus.builder()
                .targetPeriod(targetPeriod)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .lastPerformanceData(entity)
                .build();

        when(performanceDataFacilityStatusQueryService.getPerformanceDataFacilityStatus(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityRepository.save(entity)).thenReturn(entity);

        // Invoke
        int result = service.submitPerformanceData(performanceData);

        // Verify
        assertThat(result).isEqualTo(1);
        verify(performanceDataFacilityStatusQueryService).getPerformanceDataFacilityStatus(facilityId, targetPeriodYear);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityRepository).save(entity);
        verify(performanceDataFacilityStatusRepository).save(performanceDataFacilityStatus);
    }

    @Test
    void submitPerformanceData_update() {
        final Long facilityId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final Year targetPeriodYear = Year.of(2018);
        final PerformanceDataFacilityContainer data = PerformanceDataFacilityContainer.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .baselineVariableEnergy(BigDecimal.TEN)
                        .build())
                .build();
        final PerformanceDataFacility performanceData = PerformanceDataFacility.builder()
                .data(data)
                .targetPeriodType(targetPeriodType)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .build();

        final TargetPeriod targetPeriod = TargetPeriod.builder().businessId(targetPeriodType).build();
        final PerformanceDataFacilityEntity entity = PerformanceDataFacilityEntity.builder()
                .data(data)
                .targetPeriod(targetPeriod)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .reportVersion(4)
                .build();
        final PerformanceDataFacilityStatus performanceDataFacilityStatus = PerformanceDataFacilityStatus.builder()
                .targetPeriod(targetPeriod)
                .targetPeriodYear(targetPeriodYear)
                .facilityId(facilityId)
                .lastPerformanceData(PerformanceDataFacilityEntity.builder()
                        .reportVersion(3)
                        .build())
                .variationIndicator(true)
                .build();

        when(performanceDataFacilityStatusQueryService.getPerformanceDataFacilityStatus(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(performanceDataFacilityStatus));
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(performanceDataFacilityRepository.save(entity)).thenReturn(entity);

        // Invoke
        int result = service.submitPerformanceData(performanceData);

        // Verify
        assertThat(result).isEqualTo(4);
        assertThat(performanceDataFacilityStatus.isVariationIndicator()).isFalse();
        assertThat(performanceDataFacilityStatus.getLastPerformanceData()).isEqualTo(entity);
        verify(performanceDataFacilityStatusQueryService).getPerformanceDataFacilityStatus(facilityId, targetPeriodYear);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(performanceDataFacilityRepository).save(entity);
    }
}

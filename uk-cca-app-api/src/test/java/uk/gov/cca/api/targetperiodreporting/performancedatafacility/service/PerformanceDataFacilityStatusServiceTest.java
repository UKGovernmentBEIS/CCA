package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateVariationIndicatorDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
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
    
    @Test
    void updateFacilityPerformanceDataLock_isNotFinal() {
        Long facilityId = 1L;
        Year year = Year.of(2027);
        TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        TargetPeriodYearDTO targetPeriod = TargetPeriodYearDTO.builder()
        		.businessId(targetPeriodType)
        		.secondaryReportingStartDate(LocalDate.now().minusDays(5))
        		.build();

        FacilityPerformanceDataUpdateLockDTO dto = FacilityPerformanceDataUpdateLockDTO.builder()
        		.targetPeriodYear(year)
        		.targetPeriodType(targetPeriodType)
        		.locked(true)
        		.build();

        PerformanceDataFacilityStatus status = PerformanceDataFacilityStatus.builder()
        		.lastPerformanceData(PerformanceDataFacilityEntity.builder().build())
        		.build();

        when(performanceDataFacilityStatusRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, year))
                .thenReturn(Optional.of(status));
        when(targetPeriodService.getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year))
        		.thenReturn(targetPeriod);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.updateFacilityPerformanceDataLock(facilityId, dto)
        );

        assertEquals(CcaErrorCode.PERFORMANCE_DATA_FACILITY_REPORT_UPDATE_NOT_VALID, exception.getErrorCode());
        verify(targetPeriodService).getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year);
        verify(performanceDataFacilityStatusRepository, times(1)).findByFacilityIdAndTargetPeriodYear(facilityId, year);
    }
    
    @Test
    void updateFacilityPerformanceDataLock_dateNotValid() {
        Long facilityId = 1L;
        Year year = Year.of(2027);
        TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        TargetPeriodYearDTO targetPeriod = TargetPeriodYearDTO.builder()
        		.businessId(targetPeriodType)
        		.secondaryReportingStartDate(LocalDate.now().plusDays(5))
        		.build();

        FacilityPerformanceDataUpdateLockDTO dto = FacilityPerformanceDataUpdateLockDTO.builder()
        		.targetPeriodYear(year)
        		.targetPeriodType(targetPeriodType)
        		.locked(true)
        		.build();

        PerformanceDataFacilityStatus status = PerformanceDataFacilityStatus.builder()
        		.lastPerformanceData(PerformanceDataFacilityEntity.builder()
        				.submissionType(PerformanceDataSubmissionType.PRIMARY)
        				.build())
        		.build();

        when(performanceDataFacilityStatusRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, year))
                .thenReturn(Optional.of(status));
        when(targetPeriodService.getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year))
        		.thenReturn(targetPeriod);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.updateFacilityPerformanceDataLock(facilityId, dto)
        );

        assertEquals(CcaErrorCode.PERFORMANCE_DATA_FACILITY_REPORT_UPDATE_NOT_VALID,exception.getErrorCode());
        verify(targetPeriodService).getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year);
        verify(performanceDataFacilityStatusRepository, times(1)).findByFacilityIdAndTargetPeriodYear(facilityId, year);
    }

    @Test
    void updateFacilityPerformanceDataLock() {
    	Long facilityId = 1L;
        Year year = Year.of(2027);
        TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        TargetPeriodYearDTO targetPeriod = TargetPeriodYearDTO.builder()
        		.businessId(targetPeriodType)
        		.secondaryReportingStartDate(LocalDate.now().minusDays(5))
        		.build();

        FacilityPerformanceDataUpdateLockDTO dto = FacilityPerformanceDataUpdateLockDTO.builder()
        		.targetPeriodType(targetPeriodType)
        		.targetPeriodYear(year)
        		.locked(true)
        		.build();

        PerformanceDataFacilityStatus status = PerformanceDataFacilityStatus.builder()
        		.locked(false)
        		.lastPerformanceData(PerformanceDataFacilityEntity.builder()
        				.submissionType(PerformanceDataSubmissionType.PRIMARY)
        				.build())
        		.build();

        when(performanceDataFacilityStatusRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, year))
                .thenReturn(Optional.of(status));
        when(targetPeriodService.getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year))
        		.thenReturn(targetPeriod);

        service.updateFacilityPerformanceDataLock(facilityId, dto);

        assertTrue(status.isLocked());
        verify(performanceDataFacilityStatusRepository, times(1)).findByFacilityIdAndTargetPeriodYear(facilityId, year);
        verify(targetPeriodService).getTargetPeriodByTargetPeriodTypeAndTargetYear(targetPeriodType, year);
    }
    
    @Test
    void updateFacilityPerformanceDataVariationIndicator() {
    	Long facilityId = 1L;
        Year year = Year.of(2027);

        FacilityPerformanceDataUpdateVariationIndicatorDTO dto = FacilityPerformanceDataUpdateVariationIndicatorDTO.builder()
        		.targetPeriodYear(year)
        		.variationIndicator(true)
        		.build();

        PerformanceDataFacilityStatus status = PerformanceDataFacilityStatus.builder()
        		.variationIndicator(false)
        		.build();

        when(performanceDataFacilityStatusRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, year))
                .thenReturn(Optional.of(status));

        service.updateFacilityPerformanceDataVariationIndicator(facilityId, dto);

        assertTrue(status.isVariationIndicator());
        verify(performanceDataFacilityStatusRepository, times(1)).findByFacilityIdAndTargetPeriodYear(facilityId, year);
    }
    
    @Test
    void updateFacilityPerformanceDataVariationIndicator_multiple_facility_businessIds() {
    	Set<String> facilityBusinessIds = Set.of("ADS1", "ADF2");
        Year year = Year.of(2027);

        service.updateFacilityPerformanceDataVariationIndicator(facilityBusinessIds, year);

        verify(performanceDataFacilityStatusRepository, times(1))
        .updateVariationIndicatorByFacilityBusinessIdInAndTargetPeriodYear(facilityBusinessIds, year);
    }
}

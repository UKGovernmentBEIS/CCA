package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityStatusQueryServiceTest {

    @InjectMocks
    private PerformanceDataFacilityStatusQueryService service;

    @Mock
    private PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;
    
    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void getPerformanceDataFacilityStatus() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2018);

        // Invoke
        service.getPerformanceDataFacilityStatus(facilityId, targetPeriodYear);

        // Verify
        verify(performanceDataFacilityStatusRepository).findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getLastUploadedPerformanceDataContainer() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2018);

        // Invoke
        service.getLastUploadedPerformanceDataContainer(facilityId, targetPeriodYear);

        // Verify
        verify(performanceDataFacilityStatusRepository).findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }
    
    @Test
	void getFacilityPerformanceDataStatusInfoByFacilityIdAndTargetPeriodId() {
		Long facilityId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
		LocalDate secondaryReportingStartDate = LocalDate.now();
		TargetPeriod targetPeriod = TargetPeriod.builder()
				.name("TP7 (2026)")
				.secondaryReportingStartDate(secondaryReportingStartDate)
				.build();
		AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();
		PerformanceDataFacilityStatus entityFinal = PerformanceDataFacilityStatus.builder()
				.locked(true)
				.lastPerformanceData(PerformanceDataFacilityEntity.builder()
						.reportVersion(2).id(226L)
						.submissionType(PerformanceDataSubmissionType.PRIMARY)
						.build())
				.targetPeriod(TargetPeriod.builder().name("TP7 (2026)").build())
				.build();
		PerformanceDataFacilityStatus entityInterim = PerformanceDataFacilityStatus.builder()
				.locked(true)
				.lastPerformanceData(PerformanceDataFacilityEntity.builder().reportVersion(3).id(227L).build())
				.targetPeriod(TargetPeriod.builder().name("TP7 (2026)").build())
				.build();


		when(performanceDataFacilityStatusRepository.findWithDetailsByFacilityIdAndTargetPeriodBusinessId(facilityId, targetPeriodType))
				.thenReturn(List.of(entityFinal, entityInterim));
		when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
		

		List<FacilityPerformanceDataStatusInfoDTO> result = service
				.getFacilityPerformanceDataStatusInfo(facilityId, targetPeriodType, PerformanceDataReportType.FINAL, currentUser);

		// Verify
		verify(performanceDataFacilityStatusRepository)
			.findWithDetailsByFacilityIdAndTargetPeriodBusinessId(facilityId, targetPeriodType);
		verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);		
		assertThat(result).hasSize(1);
		assertEquals(entityFinal.isLocked(), result.get(0).isLocked());
		assertEquals(entityFinal.getLastPerformanceData().getReportVersion(), result.get(0).getReportVersion());
		assertEquals(entityFinal.getTargetPeriod().getName(), result.get(0).getTargetPeriodName());
	}
    
    @Test
	void getFacilityPerformanceDataReportDetails() {
		final Long facilityId = 999L;
		final Year targetPeriodYear = Year.of(2027);


		final PerformanceDataFacilityStatus facilityPerformanceDataStatus = PerformanceDataFacilityStatus.builder()
				.lastPerformanceData(PerformanceDataFacilityEntity.builder()
						.targetPeriod(TargetPeriod.builder().name("TP7 (2027)").businessId(TargetPeriodType.TP7).build())
						.data(PerformanceDataFacilityContainer.builder()
								.calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
										.targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
										.build())
								.baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
										.totalFixedEnergy(BigDecimal.valueOf(1000))
										.build())
								.build())
						.build())
				.build();

		when(performanceDataFacilityStatusRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
				.thenReturn(Optional.of(facilityPerformanceDataStatus));


		FacilityPerformanceDataReportDetailsDTO result = service.getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear);
		
		// Verify
		verify(performanceDataFacilityStatusRepository).findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
		
		assertEquals(TargetPeriodType.TP7, result.getTargetPeriod());
		assertEquals(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET, result.getCalculatedResults().getTargetPeriodResultType());
		assertEquals(BigDecimal.valueOf(1000), result.getBaselineAndTargets().getTotalFixedEnergy());
	}
}

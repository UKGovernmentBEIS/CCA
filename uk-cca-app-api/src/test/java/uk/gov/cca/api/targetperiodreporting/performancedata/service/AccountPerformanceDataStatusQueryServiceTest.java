package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetsPreviousPerformance;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataReportRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class AccountPerformanceDataStatusQueryServiceTest {

	@Mock
	private AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;

	@InjectMocks
	private AccountPerformanceDataStatusQueryService service;

	@Mock
	private TargetPeriodService targetPeriodService;

	@Mock
	private PerformanceDataReportRepository performanceDataReportRepository;

	@Test
	void testGetAccountsForPerformanceDataReportingBySector_PRIMARY() {
		final Long sectorAssociationId = 123L;
		final Long targetPeriodId = 456L;
		final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;

		List<TargetUnitAccountBusinessInfoDTO> accounts = Arrays.asList(
				new TargetUnitAccountBusinessInfoDTO(1001L, "ADS_1-T00001"),
				new TargetUnitAccountBusinessInfoDTO(1002L, "ADS_1-T00002")
		);

		when(accountPerformanceDataStatusRepository
				.findEligibleAccountsForPrimaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId))
				.thenReturn(accounts);

		List<TargetUnitAccountBusinessInfoDTO> result = service
				.getAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId, submissionType);

		assertEquals(2, result.size());
		assertEquals(1001L, result.get(0).getAccountId());
		assertEquals("ADS_1-T00001", result.get(0).getBusinessId());
		assertEquals(1002L, result.get(1).getAccountId());
		assertEquals("ADS_1-T00002", result.get(1).getBusinessId());
		verify(accountPerformanceDataStatusRepository, times(1))
				.findEligibleAccountsForPrimaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);
	}

	@Test
	void testGetAccountsForPerformanceDataReportingBySector_SECONDARY() {
		final Long sectorAssociationId = 123L;
		final Long targetPeriodId = 456L;
		final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.SECONDARY;

		List<TargetUnitAccountBusinessInfoDTO> accounts = Arrays.asList(
				new TargetUnitAccountBusinessInfoDTO(1001L, "ADS_1-T00001"),
				new TargetUnitAccountBusinessInfoDTO(1002L, "ADS_1-T00002")
		);

		when(accountPerformanceDataStatusRepository
				.findEligibleAccountsForSecondaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId))
				.thenReturn(accounts);

		List<TargetUnitAccountBusinessInfoDTO> result = service
				.getAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId, submissionType);

		assertEquals(2, result.size());
		assertEquals(1001L, result.get(0).getAccountId());
		assertEquals("ADS_1-T00001", result.get(0).getBusinessId());
		assertEquals(1002L, result.get(1).getAccountId());
		assertEquals("ADS_1-T00002", result.get(1).getBusinessId());
		verify(accountPerformanceDataStatusRepository, times(1))
				.findEligibleAccountsForSecondaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);
	}

	@Test
	void getAccountPerformanceDataStatusInfoByAccountIdAndTargetPeriodId() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();
		AccountPerformanceDataStatus entity = AccountPerformanceDataStatus.builder()
				.locked(true)
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(2).id(226L).build())
				.targetPeriod(TargetPeriod.builder().name("TP6 (2024)").build())
				.build();

		when(accountPerformanceDataStatusRepository.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(entity));


		AccountPerformanceDataStatusInfoDTO result = service
				.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, currentUser);

		assertEquals(entity.isLocked(), result.isLocked());
		assertEquals(entity.getLastPerformanceData().getReportVersion(), result.getReportVersion());
		assertEquals(entity.getTargetPeriod().getName(), result.getTargetPeriodName());
	}

	@Test
	void getAccountPerformanceDataStatusInfoByAccountIdAndTargetPeriodId_not_found() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		AppUser currentUser = AppUser.builder().roleType(SECTOR_USER).build();

		when(accountPerformanceDataStatusRepository.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.empty());

		when(targetPeriodService.findTargetPeriodNameByTargetPeriodType(targetPeriodType)).thenReturn("TP6 (2024)");


		AccountPerformanceDataStatusInfoDTO result = service
				.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, currentUser);

		Assertions.assertFalse(result.isLocked());
		assertEquals(0, result.getReportVersion());
		assertEquals("TP6 (2024)", result.getTargetPeriodName());
	}

	@Test
	void testGetSectorAccountsPerformanceDataReport() {
		Long sectorAssociationId = 1L;
		SectorAccountsPerformanceReportSearchCriteria criteria = SectorAccountsPerformanceReportSearchCriteria.builder().targetPeriodType(TargetPeriodType.TP6)
				.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build()).build();

		SectorAccountsPerformanceReportItemDTO item1 = new SectorAccountsPerformanceReportItemDTO(1L, "ADS-T00001", "ADS operator", LocalDateTime.now(), 0, TargetPeriodResultType.OUTSTANDING, PerformanceDataSubmissionType.PRIMARY,false);
		SectorAccountsPerformanceReportItemDTO item2 = new SectorAccountsPerformanceReportItemDTO(2L, "ADS-T00002", "ADS operator 2", LocalDateTime.now(), 1, TargetPeriodResultType.BUY_OUT_REQUIRED, PerformanceDataSubmissionType.PRIMARY, false);

		SectorAccountsPerformanceReportDTO expectedResults = SectorAccountsPerformanceReportDTO.builder()
				.performanceReportItems(List.of(item1, item2))
				.total(2L)
				.build();

		when(performanceDataReportRepository.getSectorAccountsPerformanceReportBySearchCriteria(sectorAssociationId, criteria)).thenReturn(expectedResults);

		SectorAccountsPerformanceReportDTO actualResults = service.getSectorAccountsPerformanceDataReport(sectorAssociationId, criteria);

		assertEquals(expectedResults, actualResults);
		verify(performanceDataReportRepository, times(1)).getSectorAccountsPerformanceReportBySearchCriteria(sectorAssociationId, criteria);

	}

	@Test
	void getNextAccountPerformanceDataReportVersion() {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final AccountPerformanceDataStatus entity = AccountPerformanceDataStatus.builder()
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(2).build())
				.build();

		when(accountPerformanceDataStatusRepository.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(entity));

		// Invoke
		int result = service.getNextAccountPerformanceDataReportVersion(accountId, targetPeriodType);

		// Verify
		assertEquals(3, result);
	}

	@Test
	void getNextAccountPerformanceDataReportVersion_no_performance_data() {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		when(accountPerformanceDataStatusRepository.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.empty());

		// Invoke
		int result = service.getNextAccountPerformanceDataReportVersion(accountId, targetPeriodType);

		// Verify
		assertEquals(1, result);
	}

	@Test
	void getAccountPerformanceDataDetails() {
		final Long accountId = 999L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;


		final AccountPerformanceDataStatus accountPerformanceDataStatus = AccountPerformanceDataStatus.builder()
				.lastPerformanceData(PerformanceDataEntity.builder()
						.data(PerformanceDataContainer.builder()
								.performanceResult(PerformanceResult.builder()
										.tpOutcome(TargetPeriodResultType.TARGET_MET)
										.build())
								.build())
						.build())
				.build();

		when(accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(accountPerformanceDataStatus));


		AccountPerformanceReportDetailsDTO result = service.getAccountPerformanceReportDetails(accountId, targetPeriodType);
		assertEquals(TargetPeriodResultType.TARGET_MET, result.getTpOutcome());

	}

	@Test
	void getLastPerformanceBuyOutSurplusDetails() {
		final Long accountId = 999L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		final AccountPerformanceDataStatus accountPerformanceDataStatus = AccountPerformanceDataStatus.builder()
				.lastPerformanceData(PerformanceDataEntity.builder()
						.id(1L)
						.submissionType(PerformanceDataSubmissionType.PRIMARY)
						.reportVersion(2)
						.data(PerformanceDataContainer.builder()
								.targetsPreviousPerformance(TargetsPreviousPerformance.builder()
										.energyCarbonUnit(MeasurementType.ENERGY_KWH)
										.bankedSurplus(BigDecimal.ZERO)
										.build())
								.performanceResult(PerformanceResult.builder()
										.tpOutcome(TargetPeriodResultType.TARGET_MET)
										.build())
								.surplusBuyOutDetermination(SurplusBuyOutDetermination.builder()
										.surplusGained(BigDecimal.ONE)
										.priBuyOutCarbon(BigDecimal.TWO)
										.priBuyOutCost(BigDecimal.TEN)
										.totalPriBuyOutCarbon(BigDecimal.ZERO)
										.build())
								.build())
						.build())
				.build();
		final PerformanceBuyOutSurplusDetailsDTO expected = PerformanceBuyOutSurplusDetailsDTO.builder()
				.performanceDataId(1L)
				.reportVersion(2)
				.submissionType(PerformanceDataSubmissionType.PRIMARY)
				.energyCarbonUnit(MeasurementType.ENERGY_KWH)
				.tpOutcome(TargetPeriodResultType.TARGET_MET)
				.bankedSurplus(BigDecimal.ZERO)
				.surplusGained(BigDecimal.ONE)
				.priBuyOutCarbon(BigDecimal.TWO)
				.priBuyOutCost(BigDecimal.TEN)
				.totalPriBuyOutCarbon(BigDecimal.ZERO)
				.build();

		when(accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(accountPerformanceDataStatus));

		// Invoke
		PerformanceBuyOutSurplusDetailsDTO result = service
				.getLastPerformanceBuyOutSurplusDetails(accountId, targetPeriodType);

		// Verify
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void getLastUploadedReport() {
		final Long accountId = 999L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final Optional<PerformanceDataContainer> lastUploadedReport = Optional.of(PerformanceDataContainer.builder()
				.surplusBuyOutDetermination(SurplusBuyOutDetermination.builder()
						.surplusUsed(BigDecimal.TEN)
						.build())
				.build());
		when(accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(AccountPerformanceDataStatus.builder()
								.lastPerformanceData(PerformanceDataEntity.builder()
										.data(lastUploadedReport.get())
										.build())
						.build()));

		// invoke
		Optional<PerformanceDataContainer> result = service.getLastUploadedReport(accountId, targetPeriodType);

		assertEquals(lastUploadedReport,result );
		verify(accountPerformanceDataStatusRepository, times(1))
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType);
	}
}

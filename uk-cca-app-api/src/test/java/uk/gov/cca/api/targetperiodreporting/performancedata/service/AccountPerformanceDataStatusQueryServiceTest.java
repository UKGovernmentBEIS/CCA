package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.SurplusBuyOutDetermination;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetsPreviousPerformance;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataReportRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class AccountPerformanceDataStatusQueryServiceTest {

	@InjectMocks
	private AccountPerformanceDataStatusQueryService service;

	@Mock
	private AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;

	@Mock
	private TargetPeriodService targetPeriodService;

	@Mock
	private PerformanceDataReportRepository performanceDataReportRepository;

	@Mock
	private AccountPerformanceDataStatusCustomRepository accountPerformanceDataStatusCustomRepository;

	@Test
	void getAccountsForPerformanceDataReportingBySector() {
		final Long sectorAssociationId = 123L;
		final Long targetPeriodId = 456L;
		final String accountName = "accountName";
		final String accountName2 = "accountName2";

		List<TargetUnitAccountBusinessInfoDTO> accounts = Arrays.asList(
				new TargetUnitAccountBusinessInfoDTO(1001L, "ADS_1-T00001", accountName),
				new TargetUnitAccountBusinessInfoDTO(1002L, "ADS_1-T00002", accountName2)
		);

		when(accountPerformanceDataStatusRepository
				.findEligibleAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId))
				.thenReturn(accounts);

		List<TargetUnitAccountBusinessInfoDTO> result = service
				.getAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);

		assertEquals(2, result.size());
		assertEquals(1001L, result.get(0).getAccountId());
		assertEquals("ADS_1-T00001", result.get(0).getBusinessId());
		assertEquals(1002L, result.get(1).getAccountId());
		assertEquals("ADS_1-T00002", result.get(1).getBusinessId());
		verify(accountPerformanceDataStatusRepository, times(1))
				.findEligibleAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);
	}

	@Test
	void findAccountsWithPerformanceDataForTargetPeriod() {
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		final AccountPerformanceDataInfo info = AccountPerformanceDataInfo.builder()
				.accountId(1L)
				.build();

        when(accountPerformanceDataStatusCustomRepository.findAccountsWithPerformanceDataByTargetPeriod(targetPeriodType))
                .thenReturn(List.of(info));

		// Invoke
		List<AccountPerformanceDataInfo> result = service.findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType);

		// Verify
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isEqualTo(info);
        verify(accountPerformanceDataStatusCustomRepository, times(1))
                .findAccountsWithPerformanceDataByTargetPeriod(targetPeriodType);
	}

	@Test
	void findAccountsWithPerformanceDataForTargetPeriod_with_account_ids() {
		final Set<Long> accountIds = Set.of(1L);
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		final AccountPerformanceDataInfo info = AccountPerformanceDataInfo.builder()
				.accountId(1L)
				.build();

        when(accountPerformanceDataStatusCustomRepository
                .findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn(targetPeriodType, accountIds))
                .thenReturn(List.of(info));

		// Invoke
		List<AccountPerformanceDataInfo> result = service.findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType, accountIds);

		// Verify
		assertThat(result).hasSize(1);
		assertThat(result.getFirst()).isEqualTo(info);
        verify(accountPerformanceDataStatusCustomRepository, times(1))
                .findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn(targetPeriodType, accountIds);
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
	void testGetSectorAccountPerformanceDataReportList() {
		Long sectorAssociationId = 1L;
		SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder().targetPeriodType(TargetPeriodType.TP6)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build()).build();

		SectorAccountPerformanceDataReportItemDTO item1 = new SectorAccountPerformanceDataReportItemDTO(1L, "ADS-T00001", "ADS operator", LocalDateTime.now(), 0, TargetPeriodResultType.OUTSTANDING, PerformanceDataSubmissionType.PRIMARY,false);
		SectorAccountPerformanceDataReportItemDTO item2 = new SectorAccountPerformanceDataReportItemDTO(2L, "ADS-T00002", "ADS operator 2", LocalDateTime.now(), 1, TargetPeriodResultType.BUY_OUT_REQUIRED, PerformanceDataSubmissionType.PRIMARY, false);

		SectorAccountPerformanceDataReportListDTO expectedResults = SectorAccountPerformanceDataReportListDTO.builder()
				.performanceDataReportItems(List.of(item1, item2))
				.total(2L)
				.build();

		when(performanceDataReportRepository.getSectorAccountPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria)).thenReturn(expectedResults);

		SectorAccountPerformanceDataReportListDTO actualResults = service.getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria);

		assertEquals(expectedResults, actualResults);
		verify(performanceDataReportRepository, times(1)).getSectorAccountPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

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
	void getAccountPerformanceDataReportDetails() {
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


		AccountPerformanceDataReportDetailsDTO result = service.getAccountPerformanceDataReportDetails(accountId, targetPeriodType);
		assertEquals(TargetPeriodResultType.TARGET_MET, result.getTpOutcome());

	}

	@Test
	void getLastPerformanceDataBuyOutSurplusDetails() {
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
						.submissionDate(LocalDateTime.of(2025, 5, 5, 12, 0))
						.build())
				.build();
		final PerformanceDataBuyOutSurplusDetailsDTO expected = PerformanceDataBuyOutSurplusDetailsDTO.builder()
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
				.submissionDate(LocalDateTime.of(2025, 5, 5, 12, 0))
				.build();

		when(accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType))
				.thenReturn(Optional.of(accountPerformanceDataStatus));

		// Invoke
		PerformanceDataBuyOutSurplusDetailsDTO result = service
				.getLastPerformanceDataBuyOutSurplusDetails(accountId, targetPeriodType);

		// Verify
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void getLastUploadedPerformanceData() {
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
		Optional<PerformanceDataContainer> result = service.getLastUploadedPerformanceData(accountId, targetPeriodType);

		assertEquals(lastUploadedReport,result );
		verify(accountPerformanceDataStatusRepository, times(1))
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType);
	}
}

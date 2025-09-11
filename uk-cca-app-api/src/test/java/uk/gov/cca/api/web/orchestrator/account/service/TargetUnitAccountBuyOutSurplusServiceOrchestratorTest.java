package uk.gov.cca.api.web.orchestrator.account.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusExclusionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountBuyOutSurplusServiceOrchestratorTest {

	@InjectMocks
	TargetUnitAccountBuyOutSurplusServiceOrchestrator serviceOrchestrator;

	@Mock
	private BuyOutSurplusExclusionService buyOutSurplusExclusionService;

	@Mock
	private SurplusQueryService surplusQueryService;

	@Mock
	private SurplusService surplusService;

	@Mock
	private SurplusMapper surplusMapper;

	@Test
	void getBuyOutSurplusInfoByAccountId() {
		final Long accountId = 999L;
		final SurplusGainedDTO defaultSurplusGainedDTO = SurplusGainedDTO.builder()
				.surplusGained(BigDecimal.ZERO)
				.targetPeriod(TargetPeriodType.TP6)
				.hasHistory(false).build();
		final AccountBuyOutSurplusInfoDTO dto = new AccountBuyOutSurplusInfoDTO(false, List.of(defaultSurplusGainedDTO));

		when(buyOutSurplusExclusionService.isAccountExcludedFromBuyOutSurplus(accountId))
				.thenReturn(false);
		when(surplusQueryService.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId))
				.thenReturn(new ArrayList<>());

		AccountBuyOutSurplusInfoDTO result = serviceOrchestrator.getBuyOutSurplusInfoByAccountId(accountId);

		Assertions.assertEquals(List.of(defaultSurplusGainedDTO), result.getSurplusGainedDTOList());
		Assertions.assertEquals(dto, result);
		Assertions.assertFalse(result.isExcluded());

		verify( buyOutSurplusExclusionService, times(1))
				.isAccountExcludedFromBuyOutSurplus(accountId);
		verify(surplusQueryService,times(1))
				.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);

	}

	@Test
	void getAllSurplusHistoryByTargetPeriodAndAccountId() {

		long accountId = 999L;
		final List<SurplusHistoryDTO> surplusHistoryList = List.of(
				SurplusHistoryDTO.builder()
						.surplusGained(BigDecimal.TEN)
						.submitter("Regulator")
						.comments("comments")
						.build()
		);

		when(surplusQueryService.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId))
				.thenReturn(surplusHistoryList);


		List<SurplusHistoryDTO> result = serviceOrchestrator.getAllSurplusHistoryByTargetPeriodAndAccountId(TargetPeriodType.TP6, accountId);

		Assertions.assertEquals(surplusHistoryList, result);
		verify(surplusQueryService, times(1))
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId);
	}

	@Test
	void getAllSurplusHistoryByTargetPeriodAndAccountId_null_result() {

		long accountId = 999L;

		when(surplusQueryService.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId))
				.thenReturn(new ArrayList<>());

		List<SurplusHistoryDTO> result = serviceOrchestrator.getAllSurplusHistoryByTargetPeriodAndAccountId(TargetPeriodType.TP6, accountId);

		Assertions.assertEquals(new ArrayList<>(),result);
		verify(surplusQueryService, times(1))
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId);
	}

	@Test
	void createBuyOutSurplusExclusion() {
		final Long accountId = 999L;

		serviceOrchestrator.excludeAccountFromBuyOutSurplus(accountId);

		verify(buyOutSurplusExclusionService, times(1))
				.excludeAccountFromBuyOutSurplus(accountId);
	}

	@Test
	void deleteBuyOutSurplusExclusion() {
		Long accountId = 999L;

		serviceOrchestrator.removeAccountExclusionFromBuyOutSurplus(accountId);

		verify(buyOutSurplusExclusionService, times(1))
				.removeAccountExclusionFromBuyOutSurplus(accountId);
	}

	@Test
	void updateSurplusGained() {

		final Long accountId = 123L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final SurplusUpdateDTO surplusUpdateDTO = SurplusUpdateDTO.builder()
				.newSurplusGained(BigDecimal.TEN)
				.targetPeriodType(targetPeriodType)
				.comments("comments")
				.build();
		final AppUser appUser = AppUser.builder()
				.userId("submitterId")
				.firstName("sub")
				.lastName("mitter")
				.build();

		serviceOrchestrator.updateSurplusGained(surplusUpdateDTO, accountId, appUser);

		verify(surplusService, times(1))
				.updateSurplusGained(surplusUpdateDTO, accountId, appUser);
	}
}
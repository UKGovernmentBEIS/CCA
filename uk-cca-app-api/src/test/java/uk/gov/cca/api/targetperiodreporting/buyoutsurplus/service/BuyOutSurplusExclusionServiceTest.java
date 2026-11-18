package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusExclusion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusExclusionServiceTest {

	@InjectMocks
	private BuyOutSurplusExclusionService service;

	@Mock
	private BuyOutSurplusExclusionRepository repository;

	@Mock
	private SurplusQueryService surplusQueryService;

	@Test
	void getBuyOutSurplusInfoByAccountId() {
		final Long accountId = 999L;
		final SurplusGainedDTO defaultSurplusGainedDTO = SurplusGainedDTO.builder()
				.surplusGained(BigDecimal.ZERO)
				.targetPeriod(TargetPeriodType.TP6)
				.hasHistory(false).build();
		final AccountBuyOutSurplusInfoDTO dto = new AccountBuyOutSurplusInfoDTO(false, List.of(defaultSurplusGainedDTO));

		when(repository.existsByAccountId(accountId))
				.thenReturn(false);
		when(surplusQueryService.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId))
				.thenReturn(new ArrayList<>());

		AccountBuyOutSurplusInfoDTO result = service.getBuyOutSurplusInfoByAccountId(accountId);

		Assertions.assertEquals(List.of(defaultSurplusGainedDTO), result.getSurplusGainedDTOList());
		Assertions.assertEquals(dto, result);
		Assertions.assertFalse(result.isExcluded());

		verify( repository, times(1))
				.existsByAccountId(accountId);
		verify(surplusQueryService,times(1))
				.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);

	}


	@Test
	void isAccountExcludedForBuyOutFee() {

		final Long accountId =999L;
		when(repository.existsByAccountId(accountId))
				.thenReturn(false);

		boolean result = service.isAccountExcludedFromBuyOutSurplus(accountId);

		assertFalse(result);
	}

	@Test
	void testExcludeAccountFromBuyOutSurplus() {

		final Long accountId =999L;
		BuyOutSurplusExclusion exclusion = BuyOutSurplusExclusion.builder().accountId(accountId).build();
		
		when(repository.existsByAccountId(accountId))
		.thenReturn(false);

		service.excludeAccountFromBuyOutSurplus(accountId);
		
		verify(repository, times(1))
			.existsByAccountId(accountId);

		verify(repository, times(1))
			.save(exclusion);

		Assertions.assertNotNull(exclusion.getAccountId());
		Assertions.assertEquals(accountId, exclusion.getAccountId());
	}

	@Test
	void testExcludeAccountFromBuyOutSurplus_alreadyExcluded() {

		final Long accountId =999L;
		BuyOutSurplusExclusion exclusion = BuyOutSurplusExclusion.builder().accountId(accountId).build();
		
		when(repository.existsByAccountId(accountId))
		.thenReturn(true);

		service.excludeAccountFromBuyOutSurplus(accountId);
		
		verify(repository, times(1))
			.existsByAccountId(accountId);

		verify(repository, times(0))
				.save(exclusion);
	}

	@Test
	void testRemoveAccountExclusionFromBuyOutSurplus() {

		final Long accountId =999L;

		service.removeAccountExclusionFromBuyOutSurplus(accountId);

		verify(repository, times(1))
				.deleteByAccountId(accountId);
	}

}
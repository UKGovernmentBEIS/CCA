package uk.gov.cca.api.web.orchestrator.account.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.buyoutsurplus.service.BuyOutSurplusExclusionService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountBuyOutSurplusServiceOrchestratorTest {

	@InjectMocks
	TargetUnitAccountBuyOutSurplusServiceOrchestrator serviceOrchestrator;

	@Mock
	private BuyOutSurplusExclusionService buyOutSurplusExclusionService;


	@Test
	void isAccountExcludedFromBuyOutFee() {
		Long accountId = 999L;

		when(buyOutSurplusExclusionService.isAccountExcludedFromBuyOutSurplus(accountId)).thenReturn(true);

		Boolean result = serviceOrchestrator.isAccountExcludedFromBuyOutSurplus(accountId);

		Assertions.assertTrue(result);
		verify(buyOutSurplusExclusionService, times(1))
				.isAccountExcludedFromBuyOutSurplus(accountId);
	}
}
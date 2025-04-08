package uk.gov.cca.api.web.orchestrator.account.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.buyoutsurplus.service.BuyOutSurplusExclusionService;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountBuyOutSurplusServiceOrchestrator {

	private final BuyOutSurplusExclusionService buyOutSurplusExclusionService;

	public Boolean isAccountExcludedFromBuyOutSurplus(Long accountId) {
		return buyOutSurplusExclusionService.isAccountExcludedFromBuyOutSurplus(accountId);
	}
}

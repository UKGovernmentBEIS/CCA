package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusExclusion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusExclusionService {

	private final BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;

	public boolean isAccountExcludedFromBuyOutSurplus(Long accountId) {
		return buyOutSurplusExclusionRepository.existsByAccountId(accountId);
	}

	@Transactional
	public void excludeAccountFromBuyOutSurplus(Long accountId) {
		if (!isAccountExcludedFromBuyOutSurplus(accountId)) {
			buyOutSurplusExclusionRepository.save(BuyOutSurplusExclusion.builder().accountId(accountId).build());
		}
	}

	@Transactional
	public void removeAccountExclusionFromBuyOutSurplus(Long accountId) {
		buyOutSurplusExclusionRepository.deleteByAccountId(accountId);
	}

}

package uk.gov.cca.api.buyoutsurplus.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusExclusionService {

	private final BuyOutSurplusExclusionRepository repository;

	public boolean isAccountExcludedFromBuyOutSurplus(Long accountId) {
		return repository.existsByAccountId(accountId);
	}
}

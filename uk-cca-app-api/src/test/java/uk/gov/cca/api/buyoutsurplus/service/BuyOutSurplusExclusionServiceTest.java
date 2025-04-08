package uk.gov.cca.api.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusExclusionServiceTest {

	@InjectMocks
	BuyOutSurplusExclusionService service;

	@Mock
	BuyOutSurplusExclusionRepository repository;


	@Test
	void isAccountExcludedForBuyOutFee() {
		final Long accountId = 999L;

		when(repository.existsByAccountId(accountId)).thenReturn(false);

		boolean result = service.isAccountExcludedFromBuyOutSurplus(accountId);

		assertFalse(result);
	}
}
package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionQueryServiceTest {

	@InjectMocks
	private BuyOutSurplusTransactionQueryService service;

	@Mock
	private BuyOutSurplusTransactionRepository repository;

	@Test
	void getAccountIdByBuyOutSurplusTransactionId() {
		final Long transactionId = 99L;
		final Long accountId = 1L;
		when(repository.findAccountIdByTransactionId(transactionId))
				.thenReturn(Optional.of(accountId));

		Long result = service.getAccountIdByBuyOutSurplusTransactionId(transactionId);

		assertThat(result).isEqualTo(accountId);
		verify(repository, times(1)).findAccountIdByTransactionId(transactionId);
	}
}
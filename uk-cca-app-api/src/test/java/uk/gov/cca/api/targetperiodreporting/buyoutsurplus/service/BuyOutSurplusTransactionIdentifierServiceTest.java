package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionIdentifier;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionIdentifierRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionIdentifierServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;

    @Mock
    private BuyOutSurplusTransactionIdentifierRepository buyOutSurplusTransactionIdentifierRepository;

    @Test
    void generateTransactionCode() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final BuyOutSurplusTransactionIdentifier identifier = BuyOutSurplusTransactionIdentifier.builder()
                .transactionId(2L)
                .build();

        when(buyOutSurplusTransactionIdentifierRepository.findByTargetPeriodType(targetPeriodType))
                .thenReturn(Optional.of(identifier));

        // Invoke
        String result = buyOutSurplusTransactionIdentifierService.generateTransactionCode(targetPeriodType);

        // Verify
        assertThat(result).isEqualTo("CCA060003");
        verify(buyOutSurplusTransactionIdentifierRepository, times(1))
                .findByTargetPeriodType(targetPeriodType);
    }
}

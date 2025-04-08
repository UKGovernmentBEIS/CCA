package uk.gov.cca.api.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutTransactionIdentifier;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutTransactionIdentifierRepository;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutTransactionIdentifierServiceTest {

    @InjectMocks
    private BuyOutTransactionIdentifierService buyOutTransactionIdentifierService;

    @Mock
    private BuyOutTransactionIdentifierRepository buyOutTransactionIdentifierRepository;

    @Test
    void generateTransactionId() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final BuyOutTransactionIdentifier identifier = BuyOutTransactionIdentifier.builder()
                .transactionId(2L)
                .build();

        when(buyOutTransactionIdentifierRepository.findByTargetPeriodType(targetPeriodType))
                .thenReturn(Optional.of(identifier));

        // Invoke
        String result = buyOutTransactionIdentifierService.generateTransactionId(targetPeriodType);

        // Verify
        assertThat(result).isEqualTo("CCA060003");
        verify(buyOutTransactionIdentifierRepository, times(1))
                .findByTargetPeriodType(targetPeriodType);
    }
}

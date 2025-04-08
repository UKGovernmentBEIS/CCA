package uk.gov.cca.api.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutPaymentStatus;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusEntity;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.buyoutsurplus.domain.dto.BuyOutSurplusDTO;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutSurplusRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusServiceTest {

    @InjectMocks
    private BuyOutSurplusService buyOutSurplusService;

    @Mock
    private BuyOutSurplusRepository buyOutSurplusRepository;

    @Test
    void createBuyOutSurplus() {
        final BuyOutSurplusDTO dto = BuyOutSurplusDTO.builder()
                .performanceDataId(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.ONE)
                        .invoicedSurplusGained(BigDecimal.ZERO)
                        .invoicedPreviousPaidFees(BigDecimal.TWO)
                        .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                        .build())
                .fileDocumentUuid("fileDocumentUuid")
                .build();

        final BuyOutSurplusEntity entity = BuyOutSurplusEntity.builder()
                .performanceDataId(1L)
                .transactionId("CCA060020")
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.ONE)
                        .invoicedSurplusGained(BigDecimal.ZERO)
                        .invoicedPreviousPaidFees(BigDecimal.TWO)
                        .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                        .build())
                .fileDocumentUuid("fileDocumentUuid")
                .build();

        // Invoke
        buyOutSurplusService.createBuyOutSurplus(dto);

        // Verify
        verify(buyOutSurplusRepository, times(1)).save(entity);
    }

    @Test
    void updateBuyOutSurplusToTerminated() {
        final Long accountId = 1L;

        final Set<BuyOutPaymentStatus> paymentStatuses = Set.of(BuyOutPaymentStatus.AWAITING_PAYMENT,
                BuyOutPaymentStatus.AWAITING_REFUND);
        final List<BuyOutSurplusEntity> entities = List.of(
                BuyOutSurplusEntity.builder()
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedPaymentDeadline(LocalDate.now())
                                .build())
                        .paymentStatus(BuyOutPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusEntity.builder()
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedPaymentDeadline(LocalDate.now())
                                .build())
                        .paymentStatus(BuyOutPaymentStatus.AWAITING_REFUND).build()
        );
        final List<BuyOutSurplusEntity> updatedEntities = List.of(
                BuyOutSurplusEntity.builder()
                        .paymentStatus(BuyOutPaymentStatus.TERMINATED).build(),
                BuyOutSurplusEntity.builder()
                        .paymentStatus(BuyOutPaymentStatus.TERMINATED).build()
        );

        when(buyOutSurplusRepository.findAllByAccountIdAndPaymentStatusIn(accountId, paymentStatuses))
                .thenReturn(entities);

        // Invoke
        buyOutSurplusService.updateBuyOutSurplusToTerminated(accountId);

        // Verify
        verify(buyOutSurplusRepository, times(1))
                .findAllByAccountIdAndPaymentStatusIn(accountId, paymentStatuses);
        verify(buyOutSurplusRepository, times(1)).saveAll(updatedEntities);
    }
}

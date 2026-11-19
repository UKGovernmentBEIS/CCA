package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailQueryServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;

    @Test
    void getBuyOutSurplusTransactionDetailsByIds() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionDetail> transactionDetails = List.of(
                BuyOutSurplusTransactionDetail.builder().id(1L).performanceDataId(11L).buyOutFee(BigDecimal.ONE).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetail.builder().id(2L).performanceDataId(22L).buyOutFee(BigDecimal.TWO).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        when(buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids)).thenReturn(transactionDetails);

        // Invoke
        List<BuyOutSurplusTransactionDetailInfoDTO> results = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsByIds(ids);

        // Verify
        assertThat(results).containsExactlyInAnyOrderElementsOf(List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).performanceDataId(11L).buyOutFee(BigDecimal.ONE).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).performanceDataId(22L).buyOutFee(BigDecimal.TWO).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        ));
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findAllByIdIn(ids);
    }

    @Test
    void getBuyOutSurplusTransactionDetailHistory() {
        final Long id = 1L;

        final BuyOutSurplusTransactionDetailAmountChangedHistoryPayload payload = BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.builder()
                .type(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)
                .amount(BigDecimal.ONE)
                .comments("My comments")
                .evidenceFiles(Map.of(UUID.randomUUID(), "evidence"))
                .build();
        final BuyOutSurplusTransactionDetailHistory history = BuyOutSurplusTransactionDetailHistory.builder()
                .id(22L)
                .changeType(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)
                .payload(payload)
                .submitterId("submitterId")
                .submitter("submitter")
                .submissionDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .build();
        final BuyOutSurplusTransactionDetail detail = BuyOutSurplusTransactionDetail.builder()
                .transactionHistoryList(List.of(history))
                .build();

        final BuyOutSurplusTransactionDetailHistoryDTO historyDTO = BuyOutSurplusTransactionDetailHistoryDTO.builder()
                .id(22L)
                .submitter("submitter")
                .submissionDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .payload(payload)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findById(id))
                .thenReturn(Optional.of(detail));

        // Invoke
        List<BuyOutSurplusTransactionDetailHistoryDTO> result = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailHistory(id);

        // Verify
        assertThat(result).containsExactly(historyDTO);
    }
}

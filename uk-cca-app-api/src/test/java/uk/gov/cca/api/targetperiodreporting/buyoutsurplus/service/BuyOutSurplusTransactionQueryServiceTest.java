package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.HistoryEntity;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionReceivedAmountHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionQueryServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionQueryService service;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private FileEvidenceService fileEvidenceService;

    private static final BuyOutSurplusTransactionHistoryMapper HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    @Test
    void getAccountIdByBuyOutSurplusTransactionId() {
        Long transactionId = 99L;
        Long accountId = 1L;

        when(buyOutSurplusTransactionRepository.findAccountIdByTransactionId(transactionId))
                .thenReturn(Optional.of(accountId));

        // Invoke
        Long result = service.getAccountIdByBuyOutSurplusTransactionId(transactionId);

        // Verify
        assertThat(result).isEqualTo(accountId);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findAccountIdByTransactionId(transactionId);
    }

    @Test
    void getBuyOutSurplusTransactionReceivedAmountHistory() {
        final Long transactionId = 99L;
        final Long accountId = 1L;
        final String transactionCode = "CCA060020";
        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(BigDecimal.valueOf(350))
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder().id(22L).buyOutFee(BigDecimal.valueOf(445)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                        BuyOutSurplusTransactionDetail.builder().id(23L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                        BuyOutSurplusTransactionDetail.builder().id(24L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
                ))
                .build();
        final List<BuyOutSurplusTransactionHistory> receivedAmountHistoryList = Stream.of(
                        createHistory(transaction, BigDecimal.valueOf(100), BigDecimal.ZERO, 1L),
                        createHistory(transaction, BigDecimal.valueOf(50), BigDecimal.valueOf(300), 3L),
                        createHistory(transaction, BigDecimal.valueOf(200), BigDecimal.valueOf(100), 2L))
                .sorted(Comparator.comparing(HistoryEntity::getSubmissionDate).reversed())
                .toList();
        transaction.setTransactionHistoryList(receivedAmountHistoryList);

        List<BuyOutSurplusTransactionReceivedAmountHistoryDTO> expected = receivedAmountHistoryList.stream()
                .map(HISTORY_MAPPER::toBuyOutSurplusTransactionReceivedAmountHistoryDTO)
                .toList();

        when(buyOutSurplusTransactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // invoke
        List<BuyOutSurplusTransactionReceivedAmountHistoryDTO> result = service.getBuyOutSurplusTransactionReceivedAmountHistory(transactionId);

        // verify
        verify(fileEvidenceService, times(1)).cleanUpUnusedFilesAsync();
        verify(buyOutSurplusTransactionRepository, times(1)).findById(transactionId);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getBuyOutSurplusTransactionReceivedAmountHistory_no_history_no_fee() {
        final Long transactionId = 99L;
        final Long accountId = 1L;
        final String transactionCode = "CCA060020";
        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(BigDecimal.valueOf(350))
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder().id(22L).buyOutFee(BigDecimal.valueOf(445)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                        BuyOutSurplusTransactionDetail.builder().id(23L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                        BuyOutSurplusTransactionDetail.builder().id(24L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
                ))
                .transactionHistoryList(List.of())
                .build();

        when(buyOutSurplusTransactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // invoke
        List<BuyOutSurplusTransactionReceivedAmountHistoryDTO> result = service.getBuyOutSurplusTransactionReceivedAmountHistory(transactionId);

        // verify
        verify(fileEvidenceService, times(1)).cleanUpUnusedFilesAsync();
        verify(buyOutSurplusTransactionRepository, times(1)).findById(transactionId);
        assertThat(result).isEmpty();
    }

    @Test
    void getBuyOutSurplusTransactionHistoryEvidenceFiles() {
        Long transactionId = 99L;
        Long accountId = 1L;
        String transactionCode = "CCA060020";

        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(BigDecimal.valueOf(350))
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder().id(22L).buyOutFee(BigDecimal.valueOf(445)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                        BuyOutSurplusTransactionDetail.builder().id(23L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                        BuyOutSurplusTransactionDetail.builder().id(24L).buyOutFee(BigDecimal.valueOf(555)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
                ))
                .build();

        List<BuyOutSurplusTransactionHistory> receivedAmountHistoryList = Stream.of(
                        createHistory(transaction, BigDecimal.valueOf(100), BigDecimal.ZERO, 1L),
                        createHistory(transaction, BigDecimal.valueOf(50), BigDecimal.valueOf(300), 3L),
                        createHistory(transaction, BigDecimal.valueOf(200), BigDecimal.valueOf(100), 2L))
                .sorted(Comparator.comparing(HistoryEntity::getSubmissionDate).reversed())
                .toList();
        transaction.setTransactionHistoryList(receivedAmountHistoryList);

        Set<UUID> expected = receivedAmountHistoryList.stream()
                .filter(hs -> hs.getChangeType() == BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .map(hs -> hs.getPayload().getEvidenceFiles())
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        when(buyOutSurplusTransactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // invoke
        Set<UUID> result = service.getBuyOutSurplusTransactionHistoryEvidenceFiles(transactionId);

        // verify
        verify(buyOutSurplusTransactionRepository, times(1)).findById(transactionId);
        assertThat(result).isEqualTo(expected);
    }

    private BuyOutSurplusTransactionHistory createHistory(BuyOutSurplusTransaction transaction, BigDecimal adjustmentAmount, BigDecimal previousReceivedAmount, Long order) {
        return BuyOutSurplusTransactionHistory.builder()
                .transaction(transaction)
                .changeType(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .payload(BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                        .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                        .adjustmentAmount(adjustmentAmount)
                        .previousReceivedAmount(previousReceivedAmount)
                        .paymentDate(LocalDate.now())
                        .evidenceFiles(Map.of(UUID.randomUUID(), "fileName" + order))
                        .comments("bla bla")
                        .build())
                .submitterId("submitterId")
                .submitter("submitter")
                .submissionDate(LocalDateTime.now().plusDays(order))
                .build();
    }
}
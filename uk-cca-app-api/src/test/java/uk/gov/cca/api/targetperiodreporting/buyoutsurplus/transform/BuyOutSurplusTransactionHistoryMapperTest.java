package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionReceivedAmountHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionHistoryMapperTest {

    private static final BuyOutSurplusTransactionHistoryMapper MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    @Test
    void toBuyOutSurplusTransactionReceivedAmountHistoryDTO() {
        Long transactionId = 99L;
        Long accountId = 1L;
        String transactionCode = "CCA060020";
        BigDecimal adjustmentAmount = BigDecimal.valueOf(100);
        LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        LocalDateTime submissionDate = LocalDate.of(2026, 6, 5).atStartOfDay();
        String submitter = "submitter";
        String comments = "bla bla";
        Long historyId = 34L;
        Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");

        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(BigDecimal.valueOf(350))
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();

        final BuyOutSurplusTransactionHistory history = BuyOutSurplusTransactionHistory.builder()
                .id(historyId)
                .transaction(transaction)
                .changeType(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .payload(BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                        .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                        .adjustmentAmount(adjustmentAmount)
                        .previousReceivedAmount(BigDecimal.ZERO)
                        .paymentDate(paymentDate)
                        .evidenceFiles(evidenceFiles)
                        .comments(comments)
                        .build())
                .submitterId("submitterId")
                .submitter(submitter)
                .submissionDate(submissionDate)
                .build();

        final BuyOutSurplusTransactionReceivedAmountHistoryDTO expected = BuyOutSurplusTransactionReceivedAmountHistoryDTO.builder()
                .id(historyId)
                .paymentDate(paymentDate)
                .adjustmentAmount(adjustmentAmount)
                .submissionDate(submissionDate)
                .submitter(submitter)
                .evidenceFiles(evidenceFiles)
                .comments(comments)
                .build();

        // invoke
        BuyOutSurplusTransactionReceivedAmountHistoryDTO actual = MAPPER.toBuyOutSurplusTransactionReceivedAmountHistoryDTO(history);

        // verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toBuyOutSurplusTransactionReceivedAmountHistory() {
        BigDecimal adjustmentAmount = BigDecimal.valueOf(100);
        LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        String submitterId = "submitterId";
        String comments = "bla bla";
        BigDecimal previousReceivedAmount = BigDecimal.valueOf(350);
        Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        AppUser appUser = AppUser.builder()
                .userId(submitterId)
                .firstName("firstName")
                .lastName("lastName")
                .build();
        String submitter = appUser.getFullName();

        final BuyOutSurplusTransactionHistory expected = BuyOutSurplusTransactionHistory.builder()
                .changeType(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .payload(BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                        .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                        .adjustmentAmount(adjustmentAmount)
                        .previousReceivedAmount(previousReceivedAmount)
                        .paymentDate(paymentDate)
                        .evidenceFiles(evidenceFiles)
                        .comments(comments)
                        .build())
                .submitterId(submitterId)
                .submitter(submitter)
                .build();

        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .paymentDate(paymentDate)
                .adjustmentAmount(adjustmentAmount)
                .evidenceFiles(evidenceFiles)
                .comments(comments)
                .build();

        // invoke
        BuyOutSurplusTransactionHistory actual = MAPPER.toBuyOutSurplusTransactionReceivedAmountHistory(updateReceivedAmountDTO, previousReceivedAmount, appUser);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}

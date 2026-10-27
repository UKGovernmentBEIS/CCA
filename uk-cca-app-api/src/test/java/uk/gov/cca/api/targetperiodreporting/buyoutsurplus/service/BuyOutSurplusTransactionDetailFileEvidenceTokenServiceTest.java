package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailFileEvidenceTokenServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionDetailFileEvidenceTokenService fileEvidenceTokenService;

    @Mock
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Test
    void validateFileEvidenceResource() {
        final Long resourceId = 1L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        final List<BuyOutSurplusTransactionDetailHistoryDTO> transactionHistories = List.of(
                BuyOutSurplusTransactionDetailHistoryDTO.builder()
                        .payload(BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.builder()
                                .evidenceFiles(Map.of(fileEvidenceUuid, "test1", UUID.randomUUID(), "test2"))
                                .build())
                        .build(),
                BuyOutSurplusTransactionDetailHistoryDTO.builder()
                        .payload(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                                .evidenceFiles(Map.of(UUID.randomUUID(), "test11", UUID.randomUUID(), "test12", UUID.randomUUID(), "test13"))
                                .build())
                        .build()
        );

        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailHistory(resourceId))
                .thenReturn(transactionHistories);

        // Invoke
        fileEvidenceTokenService.validateFileEvidenceResource(resourceId, fileEvidenceUuid);

        // Verify
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailHistory(resourceId);
    }

    @Test
    void validateFileEvidenceResource_not_found() {
        final Long resourceId = 1L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        final List<BuyOutSurplusTransactionDetailHistoryDTO> transactionHistories = List.of(
                BuyOutSurplusTransactionDetailHistoryDTO.builder()
                        .payload(BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.builder()
                                .evidenceFiles(Map.of(UUID.randomUUID(), "test1", UUID.randomUUID(), "test2"))
                                .build())
                        .build(),
                BuyOutSurplusTransactionDetailHistoryDTO.builder()
                        .payload(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                                .evidenceFiles(Map.of(UUID.randomUUID(), "test11", UUID.randomUUID(), "test12", UUID.randomUUID(), "test13"))
                                .build())
                        .build()
        );

        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailHistory(resourceId))
                .thenReturn(transactionHistories);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> fileEvidenceTokenService.validateFileEvidenceResource(resourceId, fileEvidenceUuid));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_TRANSACTION);
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailHistory(resourceId);
    }
}

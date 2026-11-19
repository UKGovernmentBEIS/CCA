package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionUpdateService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusCommonManagementServiceTest {

    @InjectMocks
    private BuyOutSurplusCommonManagementService buyOutSurplusCommonManagementService;

    @Mock
    private BuyOutSurplusTransactionUpdateService buyOutSurplusTransactionUpdateService;

    @Mock
    private BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;

    @Test
    void terminateAwaitingPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(15)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(4L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        final String submitter = "batch run ID runId";
        final Set<Long> awaitingIds = Set.of(1L, 4L);

        // Invoke
        buyOutSurplusCommonManagementService.terminateAwaitingPayments(previousTransactions, metadata);

        // Verify
        verify(buyOutSurplusTransactionUpdateService, times(1))
                .terminateBuyOutSurplusTransactionDetails(awaitingIds, submitter);
    }

    @Test
    void terminateAwaitingPayments_noAvailableAwaitingPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        // Invoke
        buyOutSurplusCommonManagementService.terminateAwaitingPayments(previousTransactions, metadata);

        // Verify
        verifyNoInteractions(buyOutSurplusTransactionUpdateService);
    }

    @Test
    void createBuyOutSurplusTransactions() {
        final List<BuyOutSurplusTransactionCreate> dtos = List.of(
                BuyOutSurplusTransactionCreate.builder().accountId(1L).build()
        );
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;

        // Invoke
        buyOutSurplusCommonManagementService.createBuyOutSurplusTransactions(dtos, schemeVersion);

        // Verify
        verify(buyOutSurplusTransactionUpdateService, times(1))
                .createBuyOutSurplusTransactions(dtos, schemeVersion);
    }

    @Test
    void generateTransactionCode() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        when(buyOutSurplusTransactionIdentifierService.generateTransactionCode(TargetPeriodType.TP6))
                .thenReturn("transaction");

        // Invoke
        buyOutSurplusCommonManagementService.generateTransactionCode(targetPeriodType);

        // Verify
        verify(buyOutSurplusTransactionIdentifierService, times(1))
                .generateTransactionCode(TargetPeriodType.TP6);
    }
}

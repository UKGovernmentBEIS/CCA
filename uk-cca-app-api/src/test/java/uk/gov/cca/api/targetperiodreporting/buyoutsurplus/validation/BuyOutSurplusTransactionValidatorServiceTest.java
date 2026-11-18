package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionValidatorServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Test
    void validateTerminateTransactions() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build()
        );

        when(buyOutSurplusQueryService.getAllTransactionInfoByIds(ids)).thenReturn(transactions);

        // Invoke
        buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids);

        // Verify
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByIds(ids);
    }

    @Test
    void validateTerminateTransactions_payment_status_invalid() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
        );

        when(buyOutSurplusQueryService.getAllTransactionInfoByIds(ids)).thenReturn(transactions);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByIds(ids);
    }

    @Test
    void validateTerminateTransactions_transactions_not_found() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        when(buyOutSurplusQueryService.getAllTransactionInfoByIds(ids)).thenReturn(transactions);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(buyOutSurplusQueryService, times(1)).getAllTransactionInfoByIds(ids);
    }

    @Test
    void validateTransactionPaymentStatus() {
        BuyOutSurplusChargeType transactionChargeType = BuyOutSurplusChargeType.FEE;
        BuyOutSurplusPaymentStatus currentPaymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
        BuyOutSurplusPaymentStatus updatedPaymentStatus = BuyOutSurplusPaymentStatus.PAID;

        buyOutSurplusTransactionValidatorService
                .validateBuyOutSurplusTransactionPaymentStatus(transactionChargeType, currentPaymentStatus, updatedPaymentStatus);

        assertDoesNotThrow(() -> buyOutSurplusTransactionValidatorService
                .validateBuyOutSurplusTransactionPaymentStatus(transactionChargeType, currentPaymentStatus, updatedPaymentStatus));
    }
    @Test
    void validateTransactionPaymentStatus_TERMINATED() {
        BuyOutSurplusChargeType transactionChargeType = BuyOutSurplusChargeType.FEE;
        BuyOutSurplusPaymentStatus currentPaymentStatus = BuyOutSurplusPaymentStatus.TERMINATED;
        BuyOutSurplusPaymentStatus updatedPaymentStatus = BuyOutSurplusPaymentStatus.PAID;

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService
                        .validateBuyOutSurplusTransactionPaymentStatus(transactionChargeType, currentPaymentStatus, updatedPaymentStatus));

        // Verify
        assertThat(ex.getErrorCode())
                .isEqualTo(CcaErrorCode.TERMINATED_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
    }

    @Test
    void validateTransactionPaymentStatus_INVALID_PAYMENT_STATUS() {
        BuyOutSurplusChargeType transactionChargeType = BuyOutSurplusChargeType.FEE;
        BuyOutSurplusPaymentStatus currentPaymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
        BuyOutSurplusPaymentStatus updatedPaymentStatus = BuyOutSurplusPaymentStatus.REFUNDED;

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService
                        .validateBuyOutSurplusTransactionPaymentStatus(transactionChargeType, currentPaymentStatus, updatedPaymentStatus));

        // Verify
        assertThat(ex.getErrorCode())
                .isEqualTo(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
    }
}

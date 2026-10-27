package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionValidatorServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;

    @Mock
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Test
    void validateTerminateTransactions() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionDetailInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build()
        );

        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailsByIds(ids)).thenReturn(transactions);

        // Invoke
        buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids);

        // Verify
        verify(buyOutSurplusTransactionDetailQueryService, times(1)).getBuyOutSurplusTransactionDetailsByIds(ids);
    }

    @Test
    void validateTerminateTransactions_payment_status_invalid() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionDetailInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
        );

        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailsByIds(ids)).thenReturn(transactions);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        verify(buyOutSurplusTransactionDetailQueryService, times(1)).getBuyOutSurplusTransactionDetailsByIds(ids);
    }

    @Test
    void validateTerminateTransactions_transactions_not_found() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionDetailInfoDTO> transactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailsByIds(ids)).thenReturn(transactions);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(buyOutSurplusTransactionDetailQueryService, times(1)).getBuyOutSurplusTransactionDetailsByIds(ids);
    }

    @Test
    void validatePaymentStatus() {
        BuyOutSurplusChargeType transactionChargeType = BuyOutSurplusChargeType.FEE;
        BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;

        // Invoke
        buyOutSurplusTransactionValidatorService
                .validatePaymentStatus(transactionChargeType, paymentStatus);
    }

    @Test
    void isPaymentStatusValid_INVALID_PAYMENT_STATUS() {
        BuyOutSurplusChargeType transactionChargeType = BuyOutSurplusChargeType.FEE;
        BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusPaymentStatus.REFUNDED;

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionValidatorService.validatePaymentStatus(transactionChargeType, paymentStatus));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
    }
}

package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.BuyOutSurplusTransactionValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionService buyOutSurplusTransactionService;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;

    @Test
    void createBuyOutSurplusTransaction() {
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedSurplusGained(BigDecimal.ZERO)
                .invoicedPreviousPaidFees(BigDecimal.TWO)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .build();
        final BuyOutSurplusTransactionCreateDTO dto = BuyOutSurplusTransactionCreateDTO.builder()
                .performanceDataId(1L)
                .transactionCode("CCA060020")
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutSurplusContainer(container)
                .fileDocumentUuid("fileDocumentUuid")
                .build();

        final BuyOutSurplusTransaction transactionEntity = BuyOutSurplusTransaction.builder()
                .performanceDataId(1L)
                .transactionCode("CCA060020")
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutSurplusContainer(container)
                .fileDocumentUuid("fileDocumentUuid")
                .build();

        // Invoke
        buyOutSurplusTransactionService.createBuyOutSurplusTransaction(dto);

        // Verify
        verify(buyOutSurplusTransactionRepository, times(1)).save(transactionEntity);
    }

    @Test
    void terminateBuyOutSurplusTransactions() {
        final Set<Long> ids = Set.of(1L, 2L);
        final String submitter = "submitter";

        final List<BuyOutSurplusTransaction> entities = List.of(
                BuyOutSurplusTransaction.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransaction.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build()
        );

        when(buyOutSurplusTransactionRepository.findAllByIdIn(ids)).thenReturn(entities);

        // Invoke
        buyOutSurplusTransactionService.terminateBuyOutSurplusTransactions(ids, submitter);

        // Verify
        assertThat(entities.getFirst().getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.TERMINATED);
        assertThat(entities.getFirst().getTransactionHistoryList()).hasSize(1)
                .containsExactlyElementsOf(List.of(
                        BuyOutSurplusTransactionHistory.builder()
                                .submitter(submitter)
                                .transaction(entities.getFirst())
                                .payload(BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.builder()
                                        .type(BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED)
                                        .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED).build())
                                .build()
                ));
        assertThat(entities.getLast().getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.TERMINATED);
        assertThat(entities.getLast().getTransactionHistoryList()).hasSize(1)
                .containsExactlyElementsOf(List.of(
                        BuyOutSurplusTransactionHistory.builder()
                                .submitter("submitter")
                                .transaction(entities.getLast())
                                .payload(BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.builder()
                                        .type(BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED)
                                        .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED).build())
                                .build()
                ));
        verify(buyOutSurplusTransactionRepository, times(1)).findAllByIdIn(ids);
        verify(buyOutSurplusTransactionValidatorService, times(1)).validateTerminateTransactions(ids);
    }

    @Test
    void updateBuyOutSurplusPaymentStatus_throws_BusinessException() {
        final Long transactionId = 99L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder().build();
        final AppUser appUser = AppUser.builder().build();

        when(buyOutSurplusTransactionRepository.findByIdWithLock(transactionId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> buyOutSurplusTransactionService
                .updateTransactionPaymentStatus(transactionId, paymentStatusDTO, appUser)
        );
        verify(buyOutSurplusTransactionRepository, times(1))
                .findByIdWithLock(transactionId);
    }

    @Test
    void updateTransactionPaymentStatus() {
        final Long transactionId = 99L;
        final BuyOutSurplusTransaction buyOutSurplusTransaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .build())
                .build();

        final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .comments("comments")
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();

        when(buyOutSurplusTransactionRepository.findByIdWithLock(transactionId))
                .thenReturn(Optional.of(buyOutSurplusTransaction));

        // Invoke
        buyOutSurplusTransactionService.updateTransactionPaymentStatus(transactionId, statusUpdateDTO, appUser);

        verify(buyOutSurplusTransactionValidatorService, times(1))
                .validateBuyOutSurplusTransactionPaymentStatus(buyOutSurplusTransaction.getBuyOutSurplusContainer().getChargeType(),
                        BuyOutSurplusPaymentStatus.AWAITING_PAYMENT,
                        BuyOutSurplusPaymentStatus.PAID);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findByIdWithLock(transactionId);

    }

    @Test
    void updateTransactionBuyOutFee() {

        final Long transactionId = 99L;
        final BuyOutSurplusTransaction buyOutSurplusTransaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .build())
                .build();
        final BuyOutSurplusTransactionUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionUpdateAmountDTO.builder()
                .amount(BigDecimal.valueOf(10.53))
                .comments("comments")
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(buyOutSurplusTransactionRepository.findByIdWithLock(transactionId))
                .thenReturn(Optional.of(buyOutSurplusTransaction));

        buyOutSurplusTransactionService.updateTransactionAmount(transactionId,buyOutFeeUpdateDTO,appUser);

        verify(buyOutSurplusTransactionValidatorService, times(1))
                .validateBuyOutSurplusTransactionNotTerminated(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findByIdWithLock(transactionId);
    }

}

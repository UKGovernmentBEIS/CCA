package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.AccountBuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionMarkPaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.BuyOutSurplusTransactionValidatorService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.exception.CcaErrorCode.INVALID_BUYOUT_SURPLUS_TRANSACTION_CHARGE_TYPE;
import static uk.gov.cca.api.common.exception.CcaErrorCode.NEGATIVE_BUYOUT_SURPLUS_TRANSACTION_RECEIVED_AMOUNT;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionService buyOutSurplusTransactionService;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;

    @Mock
    private BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private FileEvidenceService fileEvidenceService;

    @Test
    void createBuyOutSurplusTransaction() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedSurplusGained(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ONE)
                .invoicedPreviousPaidFees(BigDecimal.TWO)
                .build();
        final AccountBuyOutSurplusTransactionCreate dto = AccountBuyOutSurplusTransactionCreate.builder()
                .transactionCode("CCA060020")
                .accountId(11L)
                .targetPeriodType(targetPeriodType)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataId(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutSurplusContainer(container)
                .build();

        final TargetPeriod targetPeriod = TargetPeriod.builder().businessId(targetPeriodType).build();

        when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);

        // Invoke
        buyOutSurplusTransactionService.createAccountBuyOutSurplusTransaction(dto);

        // Verify
        verify(targetPeriodService, times(1)).findByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusTransactionRepository, times(1)).save(any());
    }

    @Test
    void terminateBuyOutSurplusTransactionDetails() {
        final Set<Long> ids = Set.of(1L, 2L);
        final String submitter = "submitter";

        final List<BuyOutSurplusTransactionDetail> entities = List.of(
                BuyOutSurplusTransactionDetail.builder().id(1L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build(),
                BuyOutSurplusTransactionDetail.builder().id(2L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build()
        );

        when(buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids)).thenReturn(entities);

        // Invoke
        buyOutSurplusTransactionService.terminateBuyOutSurplusTransactionDetails(ids, submitter);

        // Verify
        assertThat(entities.getFirst().getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.TERMINATED);
        assertThat(entities.getFirst().getTransactionHistoryList()).hasSize(1)
                .containsExactlyElementsOf(List.of(
                        BuyOutSurplusTransactionDetailHistory.builder()
                                .submitter(submitter)
                                .transactionDetail(entities.getFirst())
                                .payload(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                                        .type(BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED)
                                        .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED).build())
                                .build()
                ));
        assertThat(entities.getLast().getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.TERMINATED);
        assertThat(entities.getLast().getTransactionHistoryList()).hasSize(1)
                .containsExactlyElementsOf(List.of(
                        BuyOutSurplusTransactionDetailHistory.builder()
                                .submitter("submitter")
                                .transactionDetail(entities.getLast())
                                .payload(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                                        .type(BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED)
                                        .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED).build())
                                .build()
                ));
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findAllByIdIn(ids);
        verify(buyOutSurplusTransactionValidatorService, times(1)).validateTerminateTransactions(ids);
    }

    @Test
    void updateTransactionDetailsPaymentStatus() {
        final Long transactionId = 99L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .paymentDate(LocalDate.of(2020, 1, 1))
                .detailIds(Set.of(1L, 2L))
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();

        BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder().chargeType(BuyOutSurplusChargeType.FEE).build();
        BuyOutSurplusTransactionDetail detail1 = BuyOutSurplusTransactionDetail.builder()
                .id(1L)
                .transaction(transaction)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();
        BuyOutSurplusTransactionDetail detail2 = BuyOutSurplusTransactionDetail.builder()
                .id(2L)
                .transaction(transaction)
                .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED)
                .build();
        List<BuyOutSurplusTransactionDetail> details = List.of(detail1, detail2);

        when(buyOutSurplusTransactionDetailRepository.findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId))
                .thenReturn(details);

        // Invoke
        buyOutSurplusTransactionService.updateTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser);

        // Verify
        assertThat(detail1.getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.PAID);
        assertThat(detail1.getPaymentDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(detail1.getTransactionHistoryList()).hasSize(1);
        assertThat(detail2.getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.TERMINATED);
        verify(fileEvidenceService, times(1)).submitFileEvidence(file.toString());
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId);
        verify(buyOutSurplusTransactionValidatorService, times(1))
                .validatePaymentStatus(BuyOutSurplusChargeType.FEE, BuyOutSurplusPaymentStatus.PAID);
    }

    @Test
    void updateTransactionDetailsPaymentStatus_not_found() {
        final Long transactionId = 99L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .detailIds(Set.of(1L, 2L))
                .build();
        final AppUser appUser = AppUser.builder().build();

        when(buyOutSurplusTransactionDetailRepository.findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId))
                .thenReturn(List.of());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId);
        verifyNoInteractions(fileEvidenceService, buyOutSurplusTransactionValidatorService);
    }

    @Test
    void updateTransactionDetailsPaymentStatus_ids_not_exist() {
        final Long transactionId = 99L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .detailIds(Set.of(1L, 2L))
                .build();
        final AppUser appUser = AppUser.builder().build();
        final BuyOutSurplusTransactionDetail detail1 = BuyOutSurplusTransactionDetail.builder()
                .id(1L)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId))
                .thenReturn(List.of(detail1));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByIdInAndTransaction_Id(Set.of(1L, 2L), transactionId);
        verifyNoInteractions(fileEvidenceService, buyOutSurplusTransactionValidatorService);
    }

    @Test
    void updateTransactionDetailAmount() {
        final Long transactionDetailId = 1L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionDetailUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionDetailUpdateAmountDTO.builder()
                .amount(BigDecimal.TEN)
                .comments("comments")
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .build();

        BuyOutSurplusTransactionDetail detail = BuyOutSurplusTransactionDetail.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .buyOutFee(BigDecimal.ONE)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findByIdForUpdate(transactionDetailId))
                .thenReturn(Optional.of(detail));

        // Invoke
        buyOutSurplusTransactionService.updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser);

        // Verify
        assertThat(detail.getBuyOutFee()).isEqualTo(BigDecimal.TEN);
        assertThat(detail.getTransactionHistoryList()).hasSize(1);
        verify(fileEvidenceService, times(1)).submitFileEvidence(file.toString());
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findByIdForUpdate(transactionDetailId);
    }

    @Test
    void updateTransactionDetailAmount_status_not_valid() {
        final Long transactionDetailId = 1L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionDetailUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionDetailUpdateAmountDTO.builder()
                .amount(BigDecimal.TEN)
                .comments("comments")
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .build();

        BuyOutSurplusTransactionDetail detail = BuyOutSurplusTransactionDetail.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED)
                .buyOutFee(BigDecimal.ONE)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findByIdForUpdate(transactionDetailId))
                .thenReturn(Optional.of(detail));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.TERMINATED_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        assertThat(detail.getBuyOutFee()).isEqualTo(BigDecimal.ONE);
        assertThat(detail.getTransactionHistoryList()).isEmpty();
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findByIdForUpdate(transactionDetailId);
        verifyNoInteractions(fileEvidenceService);
    }

    @Test
    void updateTransactionDetailAmount_not_found() {
        final Long transactionDetailId = 1L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionDetailUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionDetailUpdateAmountDTO.builder()
                .amount(BigDecimal.TEN)
                .comments("comments")
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .build();

        when(buyOutSurplusTransactionDetailRepository.findByIdForUpdate(transactionDetailId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findByIdForUpdate(transactionDetailId);
        verifyNoInteractions(fileEvidenceService);
    }

    @Test
    void markTransactionDetailsPaymentStatus() {
        final Long transactionId = 99L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionMarkPaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionMarkPaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .paymentDate(LocalDate.of(2020, 1, 1))
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();

        BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder().chargeType(BuyOutSurplusChargeType.FEE).build();
        BuyOutSurplusTransactionDetail detail1 = BuyOutSurplusTransactionDetail.builder()
                .id(1L)
                .transaction(transaction)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();
        List<BuyOutSurplusTransactionDetail> details = List.of(detail1);

        when(buyOutSurplusTransactionDetailRepository
                .findAllByPaymentStatusInAndTransaction_Id(Set.of(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, BuyOutSurplusPaymentStatus.AWAITING_REFUND), transactionId))
                .thenReturn(details);

        // Invoke
        buyOutSurplusTransactionService.markTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser);

        // Verify
        assertThat(detail1.getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.PAID);
        assertThat(detail1.getPaymentDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(detail1.getTransactionHistoryList()).hasSize(1);
        verify(buyOutSurplusTransactionValidatorService, times(1))
                .validatePaymentStatus(BuyOutSurplusChargeType.FEE, BuyOutSurplusPaymentStatus.PAID);
        verify(fileEvidenceService, times(1)).submitFileEvidence(file.toString());
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByPaymentStatusInAndTransaction_Id(Set.of(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, BuyOutSurplusPaymentStatus.AWAITING_REFUND), transactionId);
    }

    @Test
    void markTransactionDetailsPaymentStatus_empty_list() {
        final Long transactionId = 99L;
        final UUID file = UUID.randomUUID();
        final BuyOutSurplusTransactionMarkPaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionMarkPaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .paymentDate(LocalDate.of(2020, 1, 1))
                .evidenceFiles(Map.of(file, "test.pdf"))
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();

        when(buyOutSurplusTransactionDetailRepository
                .findAllByPaymentStatusInAndTransaction_Id(Set.of(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, BuyOutSurplusPaymentStatus.AWAITING_REFUND), transactionId))
                .thenReturn(List.of());

        // Invoke
        buyOutSurplusTransactionService.markTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser);

        // Verify
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByPaymentStatusInAndTransaction_Id(Set.of(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT, BuyOutSurplusPaymentStatus.AWAITING_REFUND), transactionId);
        verifyNoInteractions(fileEvidenceService, buyOutSurplusTransactionValidatorService);
    }

    @Test
    void shouldCreateAndSaveMultipleTransactions() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<TargetPeriod> targetPeriods = List.of(TargetPeriod.builder()
                        .businessId(TargetPeriodType.TP7)
                        .build(),
                TargetPeriod.builder()
                        .businessId(TargetPeriodType.TP8)
                        .build());

        final BuyOutSurplusTransactionCreate dto1 = BuyOutSurplusTransactionCreate.builder()
                .transactionCode("CCA070001")
                .accountId(11L)
                .targetPeriodType(TargetPeriodType.TP7)
                .details(new ArrayList<>())
                .build();

        final BuyOutSurplusTransactionCreate dto2 = BuyOutSurplusTransactionCreate.builder()
                .transactionCode("CCA070001")
                .accountId(11L)
                .targetPeriodType(TargetPeriodType.TP7)
                .details(new ArrayList<>())
                .build();

        when(targetPeriodService.findBySchemeVersion(schemeVersion)).thenReturn(targetPeriods);

        buyOutSurplusTransactionService.createBuyOutSurplusTransactions(List.of(dto1, dto2), schemeVersion);

        verify(targetPeriodService, times(1)).findBySchemeVersion(schemeVersion);
        verify(buyOutSurplusTransactionRepository, times(1)).saveAll(any());
    }

    @Test
    void shouldThrowExceptionWhenTargetPeriodIsNotFound() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;

        final BuyOutSurplusTransactionCreate dto = BuyOutSurplusTransactionCreate.builder()
                .transactionCode("CCA070001")
                .accountId(11L)
                .targetPeriodType(TargetPeriodType.TP7)
                .details(new ArrayList<>())
                .build();

        List<BuyOutSurplusTransactionCreate> dtos = List.of(dto);

        when(targetPeriodService.findBySchemeVersion(schemeVersion))
                .thenReturn(List.of(TargetPeriod.builder()
                        .businessId(TargetPeriodType.TP6)
                        .build()));

        assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.createBuyOutSurplusTransactions(dtos, schemeVersion));

        verify(buyOutSurplusTransactionRepository, never()).saveAll(any());
    }

    @Test
    void updateTransactionReceivedAmount() {
        final Long transactionId = 99L;
        final Long accountId = 1L;
        final String transactionCode = "CCA060020";
        final BigDecimal adjustmentAmount = BigDecimal.valueOf(150);
        final LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        final String comments = "bla bla";
        final BigDecimal receivedAmount = BigDecimal.valueOf(350);
        final Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();
        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(receivedAmount)
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();
        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .adjustmentAmount(adjustmentAmount)
                .comments(comments)
                .paymentDate(paymentDate)
                .evidenceFiles(evidenceFiles)
                .build();

        when(buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(transactionId))
                .thenReturn(Optional.of(transaction));

        // invoke
        buyOutSurplusTransactionService.updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser);

        // verify
        verify(buyOutSurplusTransactionRepository, times(1)).findBuyOutSurplusTransactionById(transactionId);
    }

    @Test
    void updateTransactionReceivedAmount_with_negative_total_amount() {
        final Long transactionId = 99L;
        final Long accountId = 1L;
        final String transactionCode = "CCA060020";
        final BigDecimal adjustmentAmount = BigDecimal.valueOf(-400);
        final LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        final String comments = "bla bla";
        final BigDecimal receivedAmount = BigDecimal.valueOf(350);
        final Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();
        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .receivedAmount(receivedAmount)
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();
        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .adjustmentAmount(adjustmentAmount)
                .comments(comments)
                .paymentDate(paymentDate)
                .evidenceFiles(evidenceFiles)
                .build();

        when(buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(transactionId))
                .thenReturn(Optional.of(transaction));

        // invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(NEGATIVE_BUYOUT_SURPLUS_TRANSACTION_RECEIVED_AMOUNT);
        verify(buyOutSurplusTransactionRepository, times(1)).findBuyOutSurplusTransactionById(transactionId);
    }

    @Test
    void updateTransactionReceivedAmount_with_not_FEE_charge_type() {
        final Long transactionId = 99L;
        final Long accountId = 1L;
        final String transactionCode = "CCA060020";
        final BigDecimal adjustmentAmount = BigDecimal.valueOf(-400);
        final LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        final String comments = "bla bla";
        final BigDecimal receivedAmount = BigDecimal.valueOf(350);
        final Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();
        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode(transactionCode)
                .accountId(accountId)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .receivedAmount(receivedAmount)
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();
        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .adjustmentAmount(adjustmentAmount)
                .comments(comments)
                .paymentDate(paymentDate)
                .evidenceFiles(evidenceFiles)
                .build();

        when(buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(transactionId))
                .thenReturn(Optional.of(transaction));

        // invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> buyOutSurplusTransactionService.updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser));

        // verify
        assertThat(businessException.getErrorCode()).isEqualTo(INVALID_BUYOUT_SURPLUS_TRANSACTION_CHARGE_TYPE);
        verify(buyOutSurplusTransactionRepository, times(1)).findBuyOutSurplusTransactionById(transactionId);
    }
}

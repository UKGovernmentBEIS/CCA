package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.AccountBuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.BuyOutSurplusTransactionValidatorService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Validated
@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionService {

    private final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    private final BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;
    private final BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;
    private final TargetPeriodService targetPeriodService;
    private final FileEvidenceService fileEvidenceService;
    private static final BuyOutSurplusTransactionMapper MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);
    private static final BuyOutSurplusTransactionHistoryMapper HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    @Transactional
    public void createAccountBuyOutSurplusTransaction(@NotNull @Valid AccountBuyOutSurplusTransactionCreate dto) {
        TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(dto.getTargetPeriodType());
        BuyOutSurplusTransaction entity = MAPPER.toBuyOutSurplusTransaction(dto);
        entity.setTargetPeriod(targetPeriod);

        buyOutSurplusTransactionRepository.save(entity);
    }

    @Transactional
    public void createBuyOutSurplusTransactions(@NotEmpty List<@NotNull @Valid BuyOutSurplusTransactionCreate> dtos, @NotNull SchemeVersion schemeVersion) {

        List<TargetPeriod> targetPeriods = targetPeriodService.findBySchemeVersion(schemeVersion);
        Map<TargetPeriodType, TargetPeriod> targetPeriodMap = targetPeriods.stream()
                .collect(Collectors.toMap(
                        TargetPeriod::getBusinessId,
                        Function.identity()
                ));

        List<BuyOutSurplusTransaction> entities = new ArrayList<>();

        dtos.forEach(dto -> {
            TargetPeriod targetPeriod = Optional
                    .ofNullable(targetPeriodMap.get(dto.getTargetPeriodType()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

            BuyOutSurplusTransaction entity = MAPPER.toBuyOutSurplusTransaction(dto, schemeVersion.getResourceType());
            entity.setTargetPeriod(targetPeriod);
            entities.add(entity);
        });

        buyOutSurplusTransactionRepository.saveAll(entities);
    }

    @Transactional
    public void terminateBuyOutSurplusTransactionDetails(@NotEmpty Set<Long> ids, @NotBlank String submitter) {
        // Validate
        buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids);

        // Update transactions
        List<BuyOutSurplusTransactionDetail> transactions = buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids);
        transactions.forEach(entity -> {
            // Update payment status
            entity.setPaymentStatus(BuyOutSurplusPaymentStatus.TERMINATED);

            BuyOutSurplusTransactionDetailHistory history = BuyOutSurplusTransactionDetailHistory.builder()
                    .submitter(submitter)
                    .payload(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                            .type(BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED)
                            .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED)
                            .build())
                    .build();
            entity.addHistory(history);
        });
    }

    @Transactional
    public void updateTransactionDetailsPaymentStatus(Long transactionId, @NotNull @Valid BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO,
                                                      AppUser appUser) {
        List<BuyOutSurplusTransactionDetail> details = buyOutSurplusTransactionDetailRepository
                .findAllByIdInAndTransaction_Id(paymentStatusDTO.getDetailIds(), transactionId);

        // Check ids existence
        Set<Long> persistedIds = details.stream().map(BuyOutSurplusTransactionDetail::getId).collect(Collectors.toSet());
        if (details.isEmpty() || persistedIds.size() != paymentStatusDTO.getDetailIds().size()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // Validate status
        final BuyOutSurplusTransaction transaction = details.getFirst().getTransaction();
        buyOutSurplusTransactionValidatorService.validatePaymentStatus(transaction.getChargeType(), paymentStatusDTO.getStatus());

        // Update transaction details
        updatePaymentStatuses(paymentStatusDTO, details, appUser);
    }

    @Transactional
    public void markTransactionDetailsPaymentStatus(Long transactionId, @NotNull @Valid BuyOutSurplusTransactionMarkPaymentStatusDTO markDTO,
                                                    AppUser appUser) {
        List<BuyOutSurplusTransactionDetail> details = buyOutSurplusTransactionDetailRepository
                .findAllByPaymentStatusInAndTransaction_Id(BuyOutSurplusPaymentStatus.getAwaitingPayments(), transactionId);

        if (details.isEmpty()) {
            return;
        }

        // Validate mark status
        final BuyOutSurplusTransaction transaction = details.getFirst().getTransaction();
        buyOutSurplusTransactionValidatorService.validatePaymentStatus(transaction.getChargeType(), markDTO.getStatus());

        // Update transaction details
        BuyOutSurplusTransactionUpdatePaymentStatusDTO updatePaymentStatusDTO = MAPPER.toBuyOutSurplusTransactionUpdatePaymentStatusDTO(markDTO);
        updatePaymentStatuses(updatePaymentStatusDTO, details, appUser);
    }

    @Transactional
    public void updateTransactionDetailAmount(Long transactionDetailId, @NotNull @Valid BuyOutSurplusTransactionDetailUpdateAmountDTO updateAmountDTO,
                                              AppUser appUser) {
        // Update
        buyOutSurplusTransactionDetailRepository.findByIdForUpdate(transactionDetailId).ifPresentOrElse(
                transaction -> {
                    if (transaction.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.TERMINATED)) {
                        throw new BusinessException(CcaErrorCode.TERMINATED_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
                    }

                    transaction.setBuyOutFee(updateAmountDTO.getAmount());
                    transaction.addHistory(HISTORY_MAPPER.toBuyOutSurplusTransactionAmountChangedHistory(updateAmountDTO, appUser));

                    // Submit evidence files
                    Optional.ofNullable(updateAmountDTO.getEvidenceFiles())
                            .ifPresent(files -> files.keySet()
                                    .forEach(uuid -> fileEvidenceService.submitFileEvidence(uuid.toString())));
                },
                () -> {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
                }
        );
    }

    @Transactional
    public void updateTransactionReceivedAmount(Long transactionId, @NotNull @Valid BuyOutSurplusTransactionUpdateReceivedAmountDTO updateAmountDTO, AppUser submitter) {
        BuyOutSurplusTransaction buyOutSurplusTransaction =
                buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(transactionId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // received amount valid only for FEE transaction
        if (buyOutSurplusTransaction.getChargeType() != BuyOutSurplusChargeType.FEE) {
            throw new BusinessException(CcaErrorCode.INVALID_BUYOUT_SURPLUS_TRANSACTION_CHARGE_TYPE);
        }

        BigDecimal adjustmentAmount = updateAmountDTO.getAdjustmentAmount();

        BigDecimal previousReceivedAmount = Optional.ofNullable(buyOutSurplusTransaction.getReceivedAmount()).orElse(BigDecimal.ZERO);
        BigDecimal currentTotalReceivedAmount = previousReceivedAmount.add(adjustmentAmount);

        // validate that the current total received amount is not negative
        if (currentTotalReceivedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(CcaErrorCode.NEGATIVE_BUYOUT_SURPLUS_TRANSACTION_RECEIVED_AMOUNT);
        }

        buyOutSurplusTransaction.setReceivedAmount(currentTotalReceivedAmount);
        buyOutSurplusTransaction.addHistory(HISTORY_MAPPER.toBuyOutSurplusTransactionReceivedAmountHistory(updateAmountDTO, previousReceivedAmount, submitter));

        // submit evidence files
        Optional.ofNullable(updateAmountDTO.getEvidenceFiles())
                .ifPresent(files -> files.keySet()
                        .forEach(uuid -> fileEvidenceService.submitFileEvidence(uuid.toString())));
    }

    private void updatePaymentStatuses(BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO,
                                       List<BuyOutSurplusTransactionDetail> details, AppUser appUser) {
        boolean areAllTerminated = details.stream()
                .allMatch(d -> d.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.TERMINATED));

        if (!areAllTerminated && !details.isEmpty()) {
            details.stream().filter(d -> !d.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.TERMINATED))
                    .forEach(detail -> {
                        BuyOutSurplusTransactionDetailHistory history = HISTORY_MAPPER
                                .toBuyOutSurplusTransactionPaymentStatusChangedHistory(paymentStatusDTO, appUser);

                        // Update
                        detail.setPaymentStatus(paymentStatusDTO.getStatus());
                        detail.setPaymentDate(paymentStatusDTO.getPaymentDate());
                        detail.addHistory(history);
                    });

            // Submit evidence files
            Optional.ofNullable(paymentStatusDTO.getEvidenceFiles())
                    .ifPresent(files -> files.keySet()
                            .forEach(uuid -> fileEvidenceService.submitFileEvidence(uuid.toString())));
        }
    }
}

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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation.BuyOutSurplusTransactionValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Validated
@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionService {

    private final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    private final BuyOutSurplusTransactionValidatorService buyOutSurplusTransactionValidatorService;
    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

    @Transactional
    public void createBuyOutSurplusTransaction(@NotNull @Valid BuyOutSurplusTransactionCreateDTO dto) {
        BuyOutSurplusTransaction entity = BUY_OUT_SURPLUS_MAPPER.toBuyOutSurplusTransaction(dto);
        buyOutSurplusTransactionRepository.save(entity);
    }

    @Transactional
    public void terminateBuyOutSurplusTransactions(@NotEmpty Set<Long> ids, @NotBlank String submitter) {
        // Validate
        buyOutSurplusTransactionValidatorService.validateTerminateTransactions(ids);

        // Update transactions
        List<BuyOutSurplusTransaction> transactions = buyOutSurplusTransactionRepository.findAllByIdIn(ids);
        transactions.forEach(entity -> {
            // Update payment status
            entity.setPaymentStatus(BuyOutSurplusPaymentStatus.TERMINATED);

            // Add history
            BuyOutSurplusTransactionHistory history = BuyOutSurplusTransactionHistory.builder()
                    .submitter(submitter)
                    .payload(BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.builder()
                            .type(BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED)
                            .paymentStatus(BuyOutSurplusPaymentStatus.TERMINATED)
                            .build())
                    .build();
            entity.addHistory(history);
        });
    }

    @Transactional
    public void updateTransactionPaymentStatus(Long transactionId, BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO, AppUser appUser) {
        buyOutSurplusTransactionRepository.findByIdWithLock(transactionId)
                .ifPresentOrElse(
                        transaction ->{
                            buyOutSurplusTransactionValidatorService.validateBuyOutSurplusTransactionPaymentStatus(
                                    transaction.getBuyOutSurplusContainer().getChargeType(),
                                    transaction.getPaymentStatus(), paymentStatusDTO.getStatus());

                            transaction.setPaymentStatus(paymentStatusDTO.getStatus());
                            transaction.setPaymentDate(paymentStatusDTO.getPaymentDate());
                            transaction.addHistory(
                                    BUY_OUT_SURPLUS_MAPPER
                                            .toBuyOutSurplusTransactionPaymentStatusChangedHistory(paymentStatusDTO, appUser));
                        },
                        () -> {
                            throw new BusinessException(RESOURCE_NOT_FOUND);
                        }
                );
    }

    @Transactional
    public void updateTransactionAmount(Long transactionId, BuyOutSurplusTransactionUpdateAmountDTO updateAmountDTO, AppUser appUser) {
        buyOutSurplusTransactionRepository.findByIdWithLock(transactionId)
                .ifPresentOrElse(
                        transaction ->{

                            buyOutSurplusTransactionValidatorService
                                    .validateBuyOutSurplusTransactionNotTerminated(transaction.getPaymentStatus());
                            transaction.setBuyOutFee(updateAmountDTO.getAmount());
                            transaction.addHistory(
                                    BUY_OUT_SURPLUS_MAPPER
                                            .toBuyOutSurplusTransactionAmountChangedHistory(updateAmountDTO, appUser));
                        },
                        () -> {
                            throw new BusinessException(RESOURCE_NOT_FOUND);
                        }
                );
    }
}

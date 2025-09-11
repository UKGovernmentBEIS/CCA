package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionValidatorService {

    private final BuyOutSurplusQueryService buyOutSurplusQueryService;

    public void validateTerminateTransactions(final Set<Long> ids) {
        final List<BuyOutSurplusTransactionInfoDTO> transactions = buyOutSurplusQueryService.getAllTransactionInfoByIds(ids);

        // Validate if transactions are in await status
        List<Long> notAwaitingIds = transactions.stream()
                .filter(t -> !BuyOutSurplusPaymentStatus.getAwaitingPayments().contains(t.getPaymentStatus()))
                .map(BuyOutSurplusTransactionInfoDTO::getId)
                .toList();
        if(!notAwaitingIds.isEmpty()) {
            throw new BusinessException(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS, notAwaitingIds);
        }

        // Validate if transactions exist
        Set<Long> transactionIds = transactions.stream().map(BuyOutSurplusTransactionInfoDTO::getId).collect(Collectors.toSet());
        Set<Long> diffs = SetUtils.disjunction(ids, transactionIds);
        if(!diffs.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, diffs);
        }
    }

    public void validateBuyOutSurplusTransactionPaymentStatus(BuyOutSurplusChargeType transactionChargeType,
                                                              BuyOutSurplusPaymentStatus currentPaymentStatus,
                                                              BuyOutSurplusPaymentStatus updatedPaymentStatus ) {

        validateBuyOutSurplusTransactionNotTerminated(currentPaymentStatus);

        final Set<BuyOutSurplusPaymentStatus> paymentStatuses = BuyOutSurplusPaymentStatus
                .getPaymentStatusesByChargeType(transactionChargeType);
        if (!paymentStatuses.contains(updatedPaymentStatus)) {
            throw new BusinessException(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        }
    }

    public void validateBuyOutSurplusTransactionNotTerminated(BuyOutSurplusPaymentStatus currentPaymentStatus ) {
        if (currentPaymentStatus.equals(BuyOutSurplusPaymentStatus.TERMINATED)) {
            throw new BusinessException(CcaErrorCode.TERMINATED_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        }
    }

}

package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionValidatorService {

    private final BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    public void validateTerminateTransactions(final Set<Long> ids) {
        final List<BuyOutSurplusTransactionDetailInfoDTO> transactions = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsByIds(ids);

        // Validate if transactions are in await status
        List<Long> notAwaitingIds = transactions.stream()
                .filter(t -> !BuyOutSurplusPaymentStatus.getAwaitingPayments().contains(t.getPaymentStatus()))
                .map(BuyOutSurplusTransactionDetailInfoDTO::getId)
                .toList();
        if(!notAwaitingIds.isEmpty()) {
            throw new BusinessException(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS, notAwaitingIds);
        }

        // Validate if transactions exist
        Set<Long> transactionIds = transactions.stream().map(BuyOutSurplusTransactionDetailInfoDTO::getId).collect(Collectors.toSet());
        Set<Long> diffs = SetUtils.disjunction(ids, transactionIds);
        if(!diffs.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, diffs);
        }
    }

    public void validatePaymentStatus(BuyOutSurplusChargeType transactionChargeType, BuyOutSurplusPaymentStatus updatedPaymentStatus) {
        final Set<BuyOutSurplusPaymentStatus> paymentStatuses = BuyOutSurplusPaymentStatus
                .getPaymentStatusesByChargeType(transactionChargeType);

        if(!paymentStatuses.contains(updatedPaymentStatus)) {
            throw new BusinessException(CcaErrorCode.INVALID_BUY_OUT_SURPLUS_TRANSACTION_PAYMENT_STATUS);
        }
    }
}

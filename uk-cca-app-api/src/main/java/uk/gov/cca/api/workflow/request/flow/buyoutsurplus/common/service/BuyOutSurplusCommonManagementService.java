package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionUpdateService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusCommonManagementService {

    private final BuyOutSurplusTransactionUpdateService buyOutSurplusTransactionUpdateService;
    private final BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;

    @Transactional
    public void terminateAwaitingPayments(final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions,
                                          final BuyOutSurplusAccountProcessingBaseRequestMetadata requestMetadata) {
        Set<Long> awaitingIds = previousTransactions.stream()
                .filter(payment -> BuyOutSurplusPaymentStatus.getAwaitingPayments().contains(payment.getPaymentStatus()))
                .map(BuyOutSurplusTransactionDetailInfoDTO::getId)
                .collect(Collectors.toSet());

        if(!awaitingIds.isEmpty()) {
            String submitter = "batch run ID " + requestMetadata.getParentRequestId();
            this.buyOutSurplusTransactionUpdateService.terminateBuyOutSurplusTransactionDetails(awaitingIds, submitter);
        }
    }

    @Transactional
    public void createBuyOutSurplusTransactions(List<BuyOutSurplusTransactionCreate> dtos, SchemeVersion schemeVersion) {
        buyOutSurplusTransactionUpdateService.createBuyOutSurplusTransactions(dtos, schemeVersion);
    }

    @Transactional
    public String generateTransactionCode(TargetPeriodType targetPeriodType) {
        return buyOutSurplusTransactionIdentifierService.generateTransactionCode(targetPeriodType);
    }
}

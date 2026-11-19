package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.AccountBuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.AccountSurplusService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.transform.BuyOutSurplusAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service.BuyOutSurplusCommonManagementService;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountManagementService {

    private final AccountSurplusService surplusService;
    private final AccountBuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;
    private final BuyOutSurplusCommonManagementService buyOutSurplusCommonManagementService;
    private static final BuyOutSurplusAccountProcessingMapper MAPPER = Mappers
            .getMapper(BuyOutSurplusAccountProcessingMapper.class);

    @Transactional
    public void terminateAwaitingPayments(final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions,
                                       final BuyOutSurplusAccountProcessingBaseRequestMetadata requestMetadata) {
        buyOutSurplusCommonManagementService.terminateAwaitingPayments(previousTransactions, requestMetadata);
    }

    @Transactional
    public String generateTransactionCode(final TargetPeriodInfoDTO targetPeriodDetails) {
        return buyOutSurplusCommonManagementService.generateTransactionCode(targetPeriodDetails.getBusinessId());
    }

    @Transactional
    public void createBankSurplus(final Long accountId, final BuyOutSurplusAccountProcessingRequestMetadata metadata,
                                  final BuyOutSurplusAccountProcessingRequestPayload requestPayload) {
        final TargetPeriodResultType resultType = requestPayload.getPerformanceData().getTpOutcome();
        final PerformanceDataSubmissionType submissionType = requestPayload.getPerformanceData().getSubmissionType();
        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(accountId)
                .surplusGained(requestPayload.getBuyOutSurplus().getBuyOutSurplusContainer().getInvoicedSurplusGained())
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + metadata.getParentRequestId())
                        .build())
                .build();

        if(resultType.equals(TargetPeriodResultType.TARGET_MET)) {
            surplusService.bankSurplus(surplusCreate);
        } else if (resultType.equals(TargetPeriodResultType.BUY_OUT_REQUIRED)
                && submissionType.equals(PerformanceDataSubmissionType.SECONDARY)) {
            // If the TPR results in “Buy-out” and there was a previously recorded positive Surplus Gained value
            surplusService.getSurplus(accountId).ifPresent(surplus -> {
                if(surplus.getSurplusGained().compareTo(BigDecimal.ZERO) > 0) {
                    surplusService.bankSurplus(surplusCreate);
                }
            });
        }
    }

    @Transactional
    public void saveBuyOutSurplusTransaction(final BuyOutSurplusResult buyOutSurplus, final Long accountId, final TargetPeriodInfoDTO targetPeriod) {
        // Persist Buy Out Surplus Transaction
        BuyOutSurplusTransactionCreate create = MAPPER
                .toBuyOutSurplusTransactionCreate(buyOutSurplus, accountId, targetPeriod.getBusinessId());

        buyOutSurplusCommonManagementService.createBuyOutSurplusTransactions(List.of(create), targetPeriod.getSchemeVersion());
    }

    @Transactional
    public void saveBuyOutSurplusProcessedData(final Long performanceDataId) {
        // Persist Buy Out Surplus Processed Data
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId);
    }
}

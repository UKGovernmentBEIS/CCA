package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.transform.BuyOutSurplusAccountProcessingMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusManagementService {

    private final SurplusQueryService surplusQueryService;
    private final SurplusService surplusService;
    private final BuyOutSurplusTransactionService buyOutSurplusTransactionService;
    private final BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;
    private final BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;
    private static final BuyOutSurplusAccountProcessingMapper MAPPER = Mappers
            .getMapper(BuyOutSurplusAccountProcessingMapper.class);

    @Transactional
    public void terminateAwaitPayments(final List<BuyOutSurplusTransactionInfoDTO> previousTransactions,
                                       final BuyOutSurplusAccountProcessingRequestMetadata requestMetadata) {
        Set<Long> awaitingIds = previousTransactions.stream()
                .filter(payment -> BuyOutSurplusPaymentStatus.getAwaitingPayments().contains(payment.getPaymentStatus()))
                .map(BuyOutSurplusTransactionInfoDTO::getId)
                .collect(Collectors.toSet());

        if(!awaitingIds.isEmpty()) {
            String submitter = "batch run ID " + requestMetadata.getParentRequestId();
            this.buyOutSurplusTransactionService.terminateBuyOutSurplusTransactions(awaitingIds, submitter);
        }
    }

    @Transactional
    public String generateTransactionCode(final TargetPeriodInfoDTO targetPeriodDetails) {
        return buyOutSurplusTransactionIdentifierService.generateTransactionCode(targetPeriodDetails.getBusinessId());
    }

    @Transactional
    public void createBankSurplus(final Long accountId, final BuyOutSurplusAccountProcessingRequestMetadata metadata,
                                  final BuyOutSurplusAccountProcessingRequestPayload requestPayload) {
        final TargetPeriodResultType resultType = requestPayload.getPerformanceData().getTpOutcome();
        final PerformanceDataSubmissionType submissionType = requestPayload.getPerformanceData().getSubmissionType();
        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(metadata.getTargetPeriodType())
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
            surplusQueryService.getSurplusByAccountIdAndTargetPeriod(accountId, metadata.getTargetPeriodType()).ifPresent(surplus -> {
                if(surplus.getSurplusGained().compareTo(BigDecimal.ZERO) > 0) {
                    surplusService.bankSurplus(surplusCreate);
                }
            });
        }
    }

    @Transactional
    public void saveBuyOutSurplusTransaction(final BuyOutSurplusResult buyOutSurplus) {
        // Persist Buy Out Surplus Transaction
        BuyOutSurplusTransactionCreateDTO dto = MAPPER.toBuyOutSurplusTransactionCreateDTO(buyOutSurplus);
        buyOutSurplusTransactionService.createBuyOutSurplusTransaction(dto);
    }

    @Transactional
    public void saveBuyOutSurplusProcessedData(final Long performanceDataId) {
        // Persist Buy Out Surplus Processed Data
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId);
    }
}

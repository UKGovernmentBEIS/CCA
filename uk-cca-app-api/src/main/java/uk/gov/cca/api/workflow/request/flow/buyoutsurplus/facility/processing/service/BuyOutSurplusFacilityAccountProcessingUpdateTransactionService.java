package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service.BuyOutSurplusManagementService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionKey;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.transform.BuyOutSurplusFacilityAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityResultsUtil;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingUpdateTransactionService {
    
    private final BuyOutSurplusTransactionService buyOutSurplusTransactionService;
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final BuyOutSurplusManagementService buyOutSurplusManagementService;
    private static final BuyOutSurplusFacilityAccountProcessingMapper MAPPER = Mappers
            .getMapper(BuyOutSurplusFacilityAccountProcessingMapper.class);
    
    public void terminateAwaitingPayments(List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults, 
            Long accountId, 
            List<Long> targetPeriodIds, 
            BuyOutSurplusAccountProcessingBaseRequestMetadata metadata) {
        Set<FacilityPeriodKey> facilityPeriods = facilityPerformanceResults.stream()
                .map(result -> new FacilityPeriodKey(result.getFacilityBusinessId(), result.getTargetPeriodType()))
                .collect(Collectors.toSet());

        List<BuyOutSurplusTransactionDetailInfoDTO> awaitTransactions = buyOutSurplusQueryService
                .getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds)
                .stream()
                .filter(transaction -> BuyOutSurplusPaymentStatus.getAwaitingPayments().contains(transaction.getPaymentStatus()))
                .filter(transaction -> facilityPeriods.contains(
                        new FacilityPeriodKey(transaction.getFacilityBusinessId(), transaction.getTargetPeriodType())))
                .map(MAPPER::toBuyOutSurplusTransactionDetailSummary).toList();
        
        if (!awaitTransactions.isEmpty()) {
            buyOutSurplusManagementService.terminateAwaitingPayments(awaitTransactions, metadata);
        }
    }
    
    public void persistNewTransactions(
            BuyOutSurplusFacilityAccountProcessingRequestPayload payload,
            BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata,
            Map<Long, Long> calculationIdsByPerformanceData) {
        
        Map<BuyOutSurplusFacilityTransactionKey, List<BuyOutSurplusFacilityPerformanceResult>> groupedByTransactionKey =
                payload.getFacilityPerformanceResults().stream()
                        .filter(BuyOutSurplusFacilityResultsUtil::isTransactionRequired)
                        .collect(Collectors.groupingBy(
                                res -> new BuyOutSurplusFacilityTransactionKey(
                                        res.getTargetPeriodType(),
                                        res.getData().getChargeType())));
        
        if (groupedByTransactionKey.isEmpty()) {
            return;
        }
        
        List<BuyOutSurplusTransactionCreate> transactions = new ArrayList<>();
        
        for (Map.Entry<BuyOutSurplusFacilityTransactionKey, List<BuyOutSurplusFacilityPerformanceResult>> entry: groupedByTransactionKey.entrySet()) {
            BuyOutSurplusFacilityTransactionKey transactionKey = entry.getKey();
            BuyOutSurplusTransactionCreate transaction =  null;
            if (transactionKey.chargeType() == BuyOutSurplusChargeType.FEE) {
                transaction = createTransaction (
                        transactionKey,
                        entry.getValue(),
                        payload.getAccountDetails().getId(),
                        metadata.getBuyOutTransactionCodes().get(transactionKey.targetPeriodType()),
                        Optional.of(payload.getInvoicedPaymentDeadlinePerTP().get(transactionKey.targetPeriodType())),
                        payload.getBuyOutOfficialNotices().get(transactionKey.targetPeriodType()).getUuid(),
                        calculationIdsByPerformanceData
                );
            } else {
                transaction = createTransaction (
                        transactionKey,
                        entry.getValue(),
                        payload.getAccountDetails().getId(),
                        metadata.getRefundTransactionCodes().get(transactionKey.targetPeriodType()),
                        Optional.empty(),
                        payload.getRefundOfficialNotices().get(transactionKey.targetPeriodType()).getUuid(),
                        calculationIdsByPerformanceData
                );
            }

            transactions.add(transaction);
        }

        buyOutSurplusTransactionService.createBuyOutSurplusTransactions(transactions, payload.getSchemeVersion());
    }
    
    private BuyOutSurplusTransactionCreate createTransaction(
            BuyOutSurplusFacilityTransactionKey key,
            List<BuyOutSurplusFacilityPerformanceResult> results,
            Long accountId,
            String transactionCode,
            Optional<LocalDate> paymentDeadline,
            String fileUuid,
            Map<Long, Long> calculationIdsByPerformanceData) {

        return BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .transactionCode(transactionCode)
                .targetPeriodType(key.targetPeriodType())
                .chargeType(key.chargeType())
                .invoicedPaymentDeadline(paymentDeadline.orElse(null))
                .fileDocumentUuid(fileUuid)
                .details(results.stream()
                        .map(result -> createTransactionDetail(result, calculationIdsByPerformanceData))
                        .toList())
                .build();
    }
    
    private BuyOutSurplusTransactionDetailCreate createTransactionDetail(
            BuyOutSurplusFacilityPerformanceResult result,
            Map<Long, Long> calculationIdsByPerformanceData) {

        var data = result.getData();

        return BuyOutSurplusTransactionDetailCreate.builder()
                .performanceDataId(result.getPerformanceDataId())
                .buyOutFee(data.getInvoicedBuyOutFee())
                .paymentStatus(result.getPaymentStatus())
                .calculationId(calculationIdsByPerformanceData.get(result.getPerformanceDataId()))
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(data.getInvoicedBuyOutFee())
                        .invoicedSurplusGained(data.getSurplusGained())
                        .priBuyOutCost(data.getBuyOutRequired().multiply(result.getTargetPeriodBuyOutCost()))
                        .invoicedPreviousPaidFees(data.getPreviousPaidFees())
                        .build())
                .build();
    }
    
    private record FacilityPeriodKey(String facilityBusinessId, TargetPeriodType targetPeriodType) {
    }
}

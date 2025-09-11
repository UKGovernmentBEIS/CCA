package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCompletedRequestActionPayload;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.Map;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusRunMapper {

    default BuyOutSurplusRunSummary toBuyOutSurplusRunSummary(Map<Long, BuyOutSurplusAccountState> buyOutSurplusAccountStates) {
        long failedAccounts = buyOutSurplusAccountStates.values().stream()
                .filter(acc -> !acc.isSucceeded())
                .count();
        long buyOutTransactions = buyOutSurplusAccountStates.values().stream()
                .filter(acc -> BuyOutSurplusPaymentStatus.AWAITING_PAYMENT.equals(acc.getPaymentStatus()))
                .count();
        long refundedTransactions = buyOutSurplusAccountStates.values().stream()
                .filter(acc -> BuyOutSurplusPaymentStatus.AWAITING_REFUND.equals(acc.getPaymentStatus()))
                .count();

        return BuyOutSurplusRunSummary.builder()
                .totalAccounts((long) buyOutSurplusAccountStates.size())
                .failedAccounts(failedAccounts)
                .buyOutTransactions(buyOutTransactions)
                .refundedTransactions(refundedTransactions)
                .build();
    }

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD)")
    BuyOutSurplusRunCompletedRequestActionPayload toBuyOutSurplusRunCompletedRequestActionPayload(BuyOutSurplusRunRequestPayload requestPayload);
}

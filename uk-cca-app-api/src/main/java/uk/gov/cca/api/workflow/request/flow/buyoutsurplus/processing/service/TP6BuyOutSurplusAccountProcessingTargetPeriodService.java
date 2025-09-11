package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.transform.BuyOutSurplusAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.service.CalculateWorkingDaysService;

@Service
@RequiredArgsConstructor
public class TP6BuyOutSurplusAccountProcessingTargetPeriodService implements BuyOutSurplusAccountProcessingTargetPeriodService {

    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final BuyOutSurplusAccountProcessingOfficialNoticeService buyOutSurplusAccountProcessingOfficialNoticeService;
    private final BuyOutSurplusManagementService buyOutSurplusManagementService;
    private final RequestService requestService;
    private final CalculateWorkingDaysService calculateWorkingDaysService;
    private static final BuyOutSurplusAccountProcessingMapper MAPPER = Mappers
            .getMapper(BuyOutSurplusAccountProcessingMapper.class);

    @Transactional
    @Override
    public void processBuyOutSurplus(Request request, BuyOutSurplusAccountState accountState) throws BuyOutSurplusAccountProcessingException {
        // Get last performance data
        PerformanceDataBuyOutSurplusDetailsDTO performanceReportDetails = accountPerformanceDataStatusQueryService
                .getLastPerformanceDataBuyOutSurplusDetails(accountState.getAccountId(), TargetPeriodType.TP6);

        // Process and calculate result
        TP6BuyOutSurplusCalculation buyOutSurplusCalculation = new TP6BuyOutSurplusCalculation(
                this.targetUnitAccountNoticeRecipients, this.buyOutSurplusQueryService,
                this.buyOutSurplusAccountProcessingOfficialNoticeService, this.buyOutSurplusManagementService,
                this.calculateWorkingDaysService);

        buyOutSurplusCalculation
                .init(request, performanceReportDetails)
                .process();

        // Create timeline
        addSubmittedAction(request);

        // Send official notice
        BuyOutSurplusAccountProcessingRequestPayload payload =
                (BuyOutSurplusAccountProcessingRequestPayload) request.getPayload();

        if(payload.isTransactionRequired()) {
            buyOutSurplusAccountProcessingOfficialNoticeService
                    .sendOfficialNotice(request, payload.getOfficialNotice(), payload.getRefundClaimForm());
        }
    }

    @Override
    public TargetPeriodType getType() {
        return TargetPeriodType.TP6;
    }

    private void addSubmittedAction(Request request) {
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                (BuyOutSurplusAccountProcessingRequestPayload) request.getPayload();
        final BuyOutSurplusAccountProcessingRequestMetadata metadata =
                (BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata();

        final TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload actionPayload = MAPPER
                .toSubmittedAction(requestPayload, metadata);

        String actionType = switch (requestPayload.getPerformanceData().getTpOutcome()) {
            case BUY_OUT_REQUIRED -> CcaRequestActionType.TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED;
            case TARGET_MET -> CcaRequestActionType.TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED;
            default -> null;
        };

        requestService.addActionToRequest(request, actionPayload, actionType, requestPayload.getSubmitterId());
    }
}

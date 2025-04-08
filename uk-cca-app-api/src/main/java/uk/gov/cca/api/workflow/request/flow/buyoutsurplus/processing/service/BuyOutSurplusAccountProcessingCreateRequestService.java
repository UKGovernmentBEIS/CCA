package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingCreateRequestService {

    private final RequestService requestService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(BuyOutSurplusAccountState accountState, String parentRequestId, String parentRequestBusinessKey) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final BuyOutSurplusRunRequestMetadata parentRequestMetadata = (BuyOutSurplusRunRequestMetadata) parentRequest.getMetadata();

        // Get account details
        TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService
                .getTargetUnitAccountDetails(accountState.getAccountId());

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountState.getAccountId()))
                .requestPayload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING_PAYLOAD)
                        .accountDetails(accountDetails)
                        .build())
                .requestMetadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountDetails.getBusinessId())
                        .targetPeriodType(parentRequestMetadata.getTargetPeriodType())
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountState.getAccountId(),
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}

package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;


import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingCreateRequestService {

	private final RequestService requestService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final BuyOutSurplusFacilityRunRequestMetadata parentRequestMetadata = (BuyOutSurplusFacilityRunRequestMetadata) parentRequest.getMetadata();
        final BuyOutSurplusFacilityRunRequestPayload parentPayload = (BuyOutSurplusFacilityRunRequestPayload) parentRequest.getPayload();
        final BuyOutSurplusFacilityAccountState accountState = parentPayload.getAccountStates().get(accountId);

        // Get account details
        TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService
                .getTargetUnitAccountDetails(accountId);

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_PAYLOAD)
                        .submitterId(parentPayload.getSubmitterId())
                        .creationDate(parentPayload.getCreationDate())
                        .targetPeriodsDetails(parentPayload.getTargetPeriodsDetails())
                        .accountDetails(accountDetails)
                        .applyPrimaryRules(parentPayload.isApplyPrimaryRules())
                        .build())
                .requestMetadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountDetails.getBusinessId())
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}

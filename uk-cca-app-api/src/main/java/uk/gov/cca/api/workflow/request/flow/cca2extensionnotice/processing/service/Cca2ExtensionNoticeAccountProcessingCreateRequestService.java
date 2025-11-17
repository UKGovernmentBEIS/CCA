package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
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
public class Cca2ExtensionNoticeAccountProcessingCreateRequestService {

    private final RequestService requestService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final Cca2ExtensionNoticeRunRequestPayload parentPayload =
                (Cca2ExtensionNoticeRunRequestPayload) parentRequest.getPayload();
        Cca2ExtensionNoticeAccountState accountState = parentPayload.getAccountStates().get(accountId);

        // Get UNA
        final UnderlyingAgreementContainer underlyingAgreementContainer = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(accountId);

        // Get Account reference details
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(accountId);

        // Start account process
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestMetadata(Cca2ExtensionNoticeAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .build())
                .requestPayload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_PAYLOAD)
                        .defaultSignatory(parentPayload.getDefaultSignatory())
                        .sectorAssociationId(accountReferenceData.getTargetUnitAccountDetails().getSectorAssociationId())
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreementContainer.getUnderlyingAgreement())
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}

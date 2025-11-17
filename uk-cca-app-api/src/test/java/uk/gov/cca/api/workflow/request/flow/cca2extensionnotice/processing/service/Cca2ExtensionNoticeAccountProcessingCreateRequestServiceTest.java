package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingCreateRequestServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingCreateRequestService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long sectorId = 2L;
        final String parentRequestId = "parentRequestId";
        final String parentRequestBusinessKey = "bk-parentRequestId";

        final String accountBusinessId = "accountBusinessId";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .accountBusinessId(accountBusinessId)
                .build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .payload(Cca2ExtensionNoticeRunRequestPayload.builder()
                        .defaultSignatory("regulatorAssignee")
                        .accountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final AccountReferenceData accountReferenceData = AccountReferenceData.builder()
                .targetUnitAccountDetails(TargetUnitAccountDetails.builder().sectorAssociationId(sectorId).build())
                .sectorAssociationDetails(SectorAssociationDetails.builder().build())
                .build();
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, "2"
        );

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestMetadata(Cca2ExtensionNoticeAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountBusinessId)
                        .build())
                .requestPayload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_PAYLOAD)
                        .defaultSignatory("regulatorAssignee")
                        .sectorAssociationId(sectorId)
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA2_EXTENSION_NOTICE_ACCOUNT_STATE, accountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .thenReturn(UnderlyingAgreementContainer.builder().underlyingAgreement(underlyingAgreement).build());
        when(accountReferenceDetailsService.getAccountReferenceData(accountId))
                .thenReturn(accountReferenceData);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        service.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainerByAccountId(accountId);
        verify(accountReferenceDetailsService, times(1))
                .getAccountReferenceData(accountId);
        verify(requestCreateAccountAndSectorResourcesService, times(1))
                .createRequestResources(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationAccountProcessingCreateRequestServiceTest {

	@InjectMocks
    private Cca2TerminationAccountProcessingCreateRequestService cca2TerminationAccountsProcessingCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long sectorAssociationId = 11L;
        final String parentRequestId = "parentRequestId";
        final String parentRequestBusinessKey = "bk-parentRequestId";

        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder().accountId(accountId).build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .metadata(Cca2TerminationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                        .cca2TerminationAccountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
        );
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA2_TERMINATION_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestMetadata(Cca2TerminationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA2_TERMINATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA2_TERMINATION_ACCOUNT_STATE, accountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(facilityDataQueryService.getAllActiveFacilityIdsByAccount(accountId)).thenReturn(List.of(1L, 2L));
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        cca2TerminationAccountsProcessingCreateRequestService.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(facilityDataQueryService, times(1)).getAllActiveFacilityIdsByAccount(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

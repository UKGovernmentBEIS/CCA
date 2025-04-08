package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
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
class BuyOutSurplusAccountProcessingCreateRequestServiceTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingCreateRequestService buyOutSurplusAccountProcessingCreateRequestService;

    @Mock
    private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long sectorAssociationId = 11L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder().accountId(accountId).build();
        final String parentRequestId = "BS-TP6010";
        final String parentRequestBusinessKey = "bk-BS-TP6010";

        final Request parentRequest = Request.builder()
                .metadata(BuyOutSurplusRunRequestMetadata.builder()
                        .targetPeriodType(targetPeriodType)
                        .build())
                .build();
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .businessId("AIC-T0041")
                .build();
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
        );
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestPayload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING_PAYLOAD)
                        .accountDetails(accountDetails)
                        .build())
                .requestMetadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountDetails.getBusinessId())
                        .targetPeriodType(targetPeriodType)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.BUY_OUT_SURPLUS_ACCOUNT_STATE, buyOutSurplusAccountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(accountDetails);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        buyOutSurplusAccountProcessingCreateRequestService.createRequest(buyOutSurplusAccountState, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(requestCreateAccountAndSectorResourcesService, times(1)).createRequestResources(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

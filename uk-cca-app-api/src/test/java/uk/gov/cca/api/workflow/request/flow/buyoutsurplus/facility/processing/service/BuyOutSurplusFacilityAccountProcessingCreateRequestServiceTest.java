package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
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
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingCreateRequestServiceTest {

	@InjectMocks
    private BuyOutSurplusFacilityAccountProcessingCreateRequestService service;

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
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final String parentRequestId = "BS-TP7010";
        final String parentRequestBusinessKey = "bk-BS-TP7010";
        final String submitterId = "regulator";

        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(targetPeriodType)
                .build();

        final BuyOutSurplusFacilityAccountState accountState =
                BuyOutSurplusFacilityAccountState.builder()
                        .build();

        final Request parentRequest = Request.builder()
                .payload(BuyOutSurplusFacilityRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .targetPeriodsDetails(List.of(targetPeriodDetails))
                        .applyPrimaryRules(true)
                        .accountStates(
                                Map.of(accountId, accountState))
                        .build())
                .metadata(BuyOutSurplusFacilityRunRequestMetadata.builder()
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
                .type(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestPayload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_PAYLOAD)
                        .submitterId(submitterId)
                        .targetPeriodsDetails(List.of(targetPeriodDetails))
                        .accountDetails(accountDetails)
                        .applyPrimaryRules(true)
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

        when(requestService.findRequestById(parentRequestId))
                .thenReturn(parentRequest);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        service.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        verify(requestService).findRequestById(parentRequestId);
        verify(accountReferenceDetailsService).getTargetUnitAccountDetails(accountId);
        verify(requestCreateAccountAndSectorResourcesService).createRequestResources(accountId);
        verify(startProcessRequestService).startProcess(requestParams);
    }
}

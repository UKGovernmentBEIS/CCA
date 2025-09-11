package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationAccountProcessingCreateRequestServiceTest {

    @InjectMocks
    private FacilityCertificationAccountProcessingCreateRequestService facilityCertificationAccountProcessingCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long sectorAssociationId = 11L;
        final String parentRequestId = "parentRequestId";
        final String parentRequestBusinessKey = "bk-parentRequestId";

        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder().accountId(accountId).build();
        final CertificationPeriodDTO certificationPeriodDetails = CertificationPeriodDTO.builder()
                .certificationPeriodType(CertificationPeriodType.CP7)
                .build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .payload(FacilityCertificationRunRequestPayload.builder()
                        .certificationPeriodDetails(certificationPeriodDetails)
                        .facilityCertificationAccountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        final List<Long> facilityIds = List.of(11L, 22L);
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
        );
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestMetadata(FacilityCertificationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .certificationPeriodDetails(certificationPeriodDetails)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE, accountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(facilityDataQueryService.getAllActiveFacilityIdsByAccount(accountId)).thenReturn(facilityIds);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        facilityCertificationAccountProcessingCreateRequestService.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        assertThat(accountState.getFacilityIds()).isEqualTo(facilityIds);
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(facilityDataQueryService, times(1)).getAllActiveFacilityIdsByAccount(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

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
        final TargetPeriodInfoDTO targetPeriod = TargetPeriodInfoDTO.builder().businessId(TargetPeriodType.TP6).build();
        final Request request = Request.builder()
                .id(parentRequestId)
                .payload(FacilityCertificationRunRequestPayload.builder()
                        .certificationPeriodDetails(certificationPeriodDetails)
                        .targetPeriod(targetPeriod)
                        .facilityCertificationAccountStates(Map.of(accountId, accountState))
                        .build())
                .build();
        final List<FacilityDTO> facilities = List.of(FacilityDTO.builder().id(11L).build());
        final UnderlyingAgreementContainer underlyingAgreement = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                                .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                                .build())
                        .build())
                .build();
        final Map<String, String> requestResources = Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
        );
        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING)
                .requestResources(requestResources)
                .requestPayload(FacilityCertificationAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING_REQUEST_PAYLOAD)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .certificationPeriodDetails(certificationPeriodDetails)
                        .targetPeriod(targetPeriod)
                        .facilities(facilities)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE, accountState
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(request);
        when(facilityDataQueryService.getActiveFacilitiesDataByAccountId(accountId)).thenReturn(facilities);
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId)).thenReturn(underlyingAgreement);
        when(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .thenReturn(requestResources);

        // Invoke
        facilityCertificationAccountProcessingCreateRequestService.createRequest(accountId, parentRequestId, parentRequestBusinessKey);

        // Verify
        assertThat(accountState.getFacilityIds()).containsExactly(11L);
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(facilityDataQueryService, times(1)).getActiveFacilitiesDataByAccountId(accountId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementContainerByAccountId(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

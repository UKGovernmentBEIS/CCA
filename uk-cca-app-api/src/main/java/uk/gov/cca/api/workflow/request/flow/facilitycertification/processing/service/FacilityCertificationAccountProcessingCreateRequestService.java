package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingCreateRequestService {

    private final RequestService requestService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final FacilityCertificationRunRequestPayload parentPayload = (FacilityCertificationRunRequestPayload) parentRequest.getPayload();
        final FacilityCertificationAccountState accountState = parentPayload.getFacilityCertificationAccountStates().get(accountId);

        final List<FacilityDTO> facilities = facilityDataQueryService.getActiveFacilitiesDataByAccountId(accountId);
        final UnderlyingAgreementContainer underlyingAgreement = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(accountId);

        // Update accountState with facilityIds
        accountState.setFacilityIds(facilities.stream().map(FacilityDTO::getId).toList());

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(FacilityCertificationAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING_REQUEST_PAYLOAD)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .certificationPeriodDetails(parentPayload.getCertificationPeriodDetails())
                        .targetPeriod(parentPayload.getTargetPeriod())
                        .facilities(facilities)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.FACILITY_CERTIFICATION_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}

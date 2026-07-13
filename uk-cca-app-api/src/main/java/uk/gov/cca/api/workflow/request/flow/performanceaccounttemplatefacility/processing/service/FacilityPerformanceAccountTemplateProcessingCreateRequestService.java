package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingCreateRequestService {

    private final RequestService requestService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(final FacilityPerformanceAccountTemplateUploadReport facilityReport, final String parentRequestId) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final FacilityPerformanceAccountTemplateDataProcessingRequestPayload parentRequestPayload =
                (FacilityPerformanceAccountTemplateDataProcessingRequestPayload) parentRequest.getPayload();

        // Get Facility related data
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityReport.getFacilityId());

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
                .requestResources(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityReport.getFacilityId()))
                .requestPayload(FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_PAYLOAD)
                        .sectorUserAssignee(parentRequestPayload.getSectorUserAssignee())
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
                        .submissionDate(parentRequestPayload.getSubmissionDate())
                        .targetYear(parentRequestPayload.getTargetYear())
                        .facility(facility)
                        .build())
                .requestMetadata(FacilityPerformanceAccountTemplateProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
                        .submittedDate(parentRequestPayload.getSubmissionDate().toLocalDate())
                        .targetYear(parentRequestPayload.getTargetYear())
                        .uploadRequestId(parentRequestPayload.getParentRequestId())
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.FACILITY_ID, facilityReport.getFacilityId(),
                        CcaBpmnProcessConstants.FACILITY_REPORT, facilityReport
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}

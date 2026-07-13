package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityProcessingCreateRequestService {

    private final RequestService requestService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;
    private final RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(final FacilityUploadReport facilityReport, final String parentRequestId) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final PerformanceDataFacilityDataProcessingRequestPayload parentRequestPayload =
                (PerformanceDataFacilityDataProcessingRequestPayload) parentRequest.getPayload();
        final TargetPeriodYear targetPeriodYear = parentRequestPayload.getTargetPeriodYear();
        final UnderlyingAgreementContainer una = parentRequestPayload.getUnderlyingAgreementAccountMap().get(facilityReport.getAccountId());

        // Get Facility related data
        final FacilityDTO facility = facilityDataQueryService.getFacilityInfoData(facilityReport.getFacilityId());
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = performanceDataFacilityReferenceDataService
                .getFacilityOriginalBaselineAndTargets(facilityReport.getFacilityBusinessId(), targetPeriodYear.getTargetYear(), una);

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_FACILITY_PROCESSING)
                .requestResources(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityReport.getFacilityId()))
                .requestPayload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_FACILITY_PROCESSING_PAYLOAD)
                        .sectorUserAssignee(parentRequestPayload.getSectorUserAssignee())
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(parentRequestPayload.getSectorAssociationInfo())
                        .targetPeriodType(parentRequestPayload.getTargetPeriodType())
                        .reportType(parentRequestPayload.getReportType())
                        .submissionType(parentRequestPayload.getSubmissionType())
                        .submissionDate(parentRequestPayload.getSubmissionDate())
                        .targetPeriodYear(targetPeriodYear)
                        .targetPeriods(parentRequestPayload.getTargetPeriods())
                        .facility(facility)
                        .baselineAndTargets(baselineAndTargets)
                        .build())
                .requestMetadata(PerformanceDataFacilityProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_FACILITY_PROCESSING)
                        .targetPeriodType(parentRequestPayload.getTargetPeriodType())
                        .reportType(parentRequestPayload.getReportType())
                        .submissionType(parentRequestPayload.getSubmissionType())
                        .submittedDate(parentRequestPayload.getSubmissionDate().toLocalDate())
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

package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityReferenceDataService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingCreateRequestServiceTest {

    @InjectMocks
    private PerformanceDataFacilityProcessingCreateRequestService performanceDataFacilityProcessingCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;

    @Mock
    private RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long facilityId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder()
                .accountId(accountId)
                .facilityId(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .build();
        final String parentRequestId = "parentRequestId";

        final TargetPeriodYear targetPeriodYear = TargetPeriodYear.builder().targetYear(Year.of(2026)).build();
        final UnderlyingAgreementContainer una = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(TargetPeriodDetailsDTO.builder().businessId(TargetPeriodType.TP7).build());
        final Request parentRequest = Request.builder()
                .payload(PerformanceDataFacilityDataProcessingRequestPayload.builder()
                        .sectorUserAssignee("sectorUserAssignee")
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .targetPeriodYear(targetPeriodYear)
                        .underlyingAgreementAccountMap(Map.of(accountId, una))
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .targetPeriods(targetPeriods)
                        .parentRequestId("parentParentRequestId")
                        .build())
                .build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();
        final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = PerformanceDataFacilityBaselineAndTargets.builder()
                .totalFixedEnergy(BigDecimal.ONE)
                .build();
        final Map<String, String> resources = Map.of("facility", "1");

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_FACILITY_PROCESSING)
                .requestResources(resources)
                .requestPayload(PerformanceDataFacilityProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_FACILITY_PROCESSING_PAYLOAD)
                        .sectorUserAssignee("sectorUserAssignee")
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .targetPeriodYear(targetPeriodYear)
                        .targetPeriods(targetPeriods)
                        .facility(facility)
                        .baselineAndTargets(baselineAndTargets)
                        .build())
                .requestMetadata(PerformanceDataFacilityProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.PERFORMANCE_DATA_FACILITY_PROCESSING)
                        .targetPeriodType(TargetPeriodType.TP7)
                        .reportType(PerformanceDataReportType.FINAL)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .submittedDate(LocalDate.of(2026, 1, 1))
                        .uploadRequestId("parentParentRequestId")
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.FACILITY_ID, facilityId,
                        CcaBpmnProcessConstants.FACILITY_REPORT, facilityReport
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(performanceDataFacilityReferenceDataService.getFacilityOriginalBaselineAndTargets(facilityBusinessId, Year.of(2026), una))
                .thenReturn(baselineAndTargets);
        when(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId)).thenReturn(resources);

        // Invoke
        performanceDataFacilityProcessingCreateRequestService.createRequest(facilityReport, parentRequestId);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(performanceDataFacilityReferenceDataService, times(1))
                .getFacilityOriginalBaselineAndTargets(facilityBusinessId, Year.of(2026), una);
        verify(requestCreateFacilityAndAccountAndSectorResourcesService, times(1))
                .createRequestResources(facilityId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

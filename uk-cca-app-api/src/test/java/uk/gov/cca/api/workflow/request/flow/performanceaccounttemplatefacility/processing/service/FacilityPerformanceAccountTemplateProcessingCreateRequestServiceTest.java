package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
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

import java.time.LocalDate;
import java.time.Year;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacilityPerformanceAccountTemplateProcessingCreateRequestServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingCreateRequestService facilityPerformanceAccountTemplateProcessingCreateRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRequest() {
        final Long accountId = 1L;
        final Long facilityId = 11L;
        final String facilityBusinessId = "facilityBusinessId";
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder()
                .accountId(accountId)
                .facilityId(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .build();
        final String parentRequestId = "parentRequestId";
        final Year targetYear = Year.of(2026);

        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final Request parentRequest = Request.builder()
                .payload(FacilityPerformanceAccountTemplateDataProcessingRequestPayload.builder()
                        .sectorUserAssignee("sectorUserAssignee")
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetYear(targetYear)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .parentRequestId("parentParentRequestId")
                        .build())
                .build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();
        final Map<String, String> resources = Map.of("facility", "1");

        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
                .requestResources(resources)
                .requestPayload(FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_PAYLOAD)
                        .sectorUserAssignee("sectorUserAssignee")
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetYear(targetYear)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .facility(facility)
                        .build())
                .requestMetadata(FacilityPerformanceAccountTemplateProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING)
                        .submittedDate(LocalDate.of(2026, 1, 1))
                        .targetYear(targetYear)
                        .uploadRequestId("parentParentRequestId")
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.FACILITY_ID, facilityId,
                        CcaBpmnProcessConstants.FACILITY_REPORT, facilityReport
                ))
                .build();

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(facilityDataQueryService.getFacilityInfoData(facilityId)).thenReturn(facility);
        when(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId)).thenReturn(resources);

        // Invoke
        facilityPerformanceAccountTemplateProcessingCreateRequestService.createRequest(facilityReport, parentRequestId);

        // Verify
        verify(requestService, times(1)).findRequestById(parentRequestId);
        verify(facilityDataQueryService, times(1)).getFacilityInfoData(facilityId);
        verify(requestCreateFacilityAndAccountAndSectorResourcesService, times(1))
                .createRequestResources(facilityId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class FacilityPerformanceAccountTemplateProcessingServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Mock
    private RequestService requestService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final Long facilityId = 11L;
        final String parentRequestId = "parentRequestId";
        final Year targetYear = Year.of(2026);
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();

        final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload =
                FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_PAYLOAD)
                        .sectorUserAssignee("sectorUserAssignee")
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetYear(targetYear)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .facility(facility)
                        .build();
        FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = FacilityPerformanceAccountTemplateUploadReport.builder()
                .build();

        List<BusinessValidationResult> results = new ArrayList<>();
        results.add(BusinessValidationResult.valid());
        final int reportVersion = 2;

        //TODO: enhance

        // Invoke
        facilityPerformanceAccountTemplateProcessingService.doProcess(requestPayload, facilityUploadReport);

        // Verify
        assertThat(facilityUploadReport.getErrors()).isEmpty();
    }

    @Test
    void doProcess_validation_error() throws BpmnExecutionException {
        //TODO:
    }


    @Test
    void markAsCompleted() {
        final Long facilityId = 11L;
        final String requestId = "requestId";
        final String parentRequestId = "parentRequestId";
        final Year targetYear = Year.of(2026);
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).build();
        final Request request = Request.builder()
                .payload(FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_PAYLOAD)
                        .sectorUserAssignee("sectorUserAssignee")
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetYear(targetYear)
                        .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                        .facility(facility)
                        .build())
                .build();
        final FacilityPerformanceAccountTemplateDataProcessingRequestPayload parentPayload =
                FacilityPerformanceAccountTemplateDataProcessingRequestPayload.builder().build();
        final Request parentRequest = Request.builder()
                .payload(parentPayload)
                .build();
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        // Invoke
        facilityPerformanceAccountTemplateProcessingService.markAsCompleted(requestId, facilityReport);

        // Verify
        assertThat(facilityReport.isSucceeded()).isTrue();
        assertThat(parentPayload.getFacilityReports()).hasSize(1);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(parentRequestId);
    }

}

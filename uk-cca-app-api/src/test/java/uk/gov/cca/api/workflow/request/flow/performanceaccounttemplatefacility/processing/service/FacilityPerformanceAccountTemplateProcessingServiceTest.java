package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service.FacilityPerformanceAccountTemplateDataService;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FacilityPerformanceAccountTemplateProcessingServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Mock
    private FacilityPerformanceAccountTemplateDataService facilityPerformanceAccountTemplateDataService;

    @Mock
    private RequestService requestService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final Long facilityId = 11L;
        final String parentRequestId = "parentRequestId";
        final Year targetYear = Year.of(2026);
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).facilityBusinessId("facilityBusinessId").build();

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
        final FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = FacilityPerformanceAccountTemplateUploadReport.builder()
                .facilityBusinessId(facility.getFacilityBusinessId())
                .accountId(1L)
                .facilityId(facilityId)
                .savingActions(List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                        .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                        .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                        .savingActionsImplemented("saving")
                        .implementationDate(LocalDate.of(2026, 1, 1))
                        .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                        .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(102.6))
                        .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                        .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                        .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                        .notes("notes")
                        .build()))
                .build();

        final Year targetPeriodYear = requestPayload.getTargetYear();
        final FacilityPerformanceAccountTemplateDataContainer container = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(facilityUploadReport.getSavingActions())
                .build();

        final int reportedVersion = 1;

        final FacilityPerformanceAccountTemplateProcessingResults expected = FacilityPerformanceAccountTemplateProcessingResults.builder()
                .container(container)
                .reportVersion(reportedVersion)
                .build();

        when(facilityPerformanceAccountTemplateDataService.submitFacilityPerformanceAccountTemplateData(container, facilityId, targetPeriodYear)).thenReturn(reportedVersion);

        // Invoke
        FacilityPerformanceAccountTemplateProcessingResults actual = facilityPerformanceAccountTemplateProcessingService.doProcess(requestPayload, facilityUploadReport);

        // Verify
        assertThat(actual).isEqualTo(expected);
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

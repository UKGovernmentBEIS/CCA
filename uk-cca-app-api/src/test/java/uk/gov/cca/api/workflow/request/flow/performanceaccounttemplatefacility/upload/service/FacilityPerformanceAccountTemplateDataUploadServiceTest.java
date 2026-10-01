package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation.FacilityPerformanceAccountTemplateDataUploadValidator;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadService service;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadValidator facilityPerformanceAccountTemplateDataUploadValidator;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadExtractCsvDataService facilityPerformanceAccountTemplateDataUploadExtractCsvService;

    @Test
    void process() {
        final LocalDateTime submissionDate = LocalDateTime.now();
        final Year targetYear = Year.of(submissionDate.getYear() - 1);
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = FacilityPerformanceAccountTemplateDataUpload.builder()
                .targetYear(targetYear)
                .files(Set.of(UUID.randomUUID()))
                .build();
        FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload taskActionPayload = FacilityPerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload.builder()
                .performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
                .build();
        final FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                .build();
        final FacilityPerformanceAccountTemplateDataUploadRequestMetadata metadata = FacilityPerformanceAccountTemplateDataUploadRequestMetadata.builder()
                .targetYear(targetYear)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().metadata(metadata).build())
                .payload(requestTaskPayload)
                .build();
        final Long facilityId = 1L;
        final String facilityBusinessId = "FDF1-F00001";
        final BigDecimal energyConsumptionOrCarbonEmissionsImpactedPercentage = new BigDecimal(10).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedExtentOfChangeImplementedPercentage = new BigDecimal(20).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedSavingsFromTheChangeImplementedPercentage = new BigDecimal(30).setScale(7, RoundingMode.HALF_DOWN);
        final FacilityPerformanceAccountTemplateSavingAction savingAction = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.PROCESS_OPTIMISATION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("Process improvements")
                .reasonsForImplementation("Tweaked a few settings to make the process more efficient")
                .implementationDate(LocalDate.of(2023, 7, 1))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(energyConsumptionOrCarbonEmissionsImpactedPercentage)
                .expectedExtentOfChangeImplementedPercentage(expectedExtentOfChangeImplementedPercentage)
                .expectedSavingsFromTheChangeImplementedPercentage(expectedSavingsFromTheChangeImplementedPercentage)
                .notes("We could only implement this measure half way through TP6")
                .build();
        Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReportsMap = Map.of(
                facilityId, FacilityPerformanceAccountTemplateUploadReport.builder()
                        .facilityId(facilityId)
                        .facilityBusinessId(facilityBusinessId)
                        .accountId(1L)
                        .savingActions(List.of(savingAction))
                        .succeeded(true)
                        .build()
        );

        when(facilityPerformanceAccountTemplateDataUploadExtractCsvService.exportAndValidateCsvData(eq(requestTaskPayload), any())).thenReturn(facilityReportsMap);

        // Invoke
        service.process(requestTask, taskActionPayload, submissionDate);

        // Verify
        assertThat(requestTaskPayload.getPerformanceAccountTemplateDataUpload()).isEqualTo(performanceAccountTemplateDataUpload);
        assertThat(requestTaskPayload.getFacilityReports()).isEqualTo(facilityReportsMap);
        verify(facilityPerformanceAccountTemplateDataUploadValidator, times(1))
                .validate(eq(requestTaskPayload), any());
        verify(facilityPerformanceAccountTemplateDataUploadExtractCsvService, times(1))
                .exportAndValidateCsvData(eq(requestTaskPayload), any());
    }
}

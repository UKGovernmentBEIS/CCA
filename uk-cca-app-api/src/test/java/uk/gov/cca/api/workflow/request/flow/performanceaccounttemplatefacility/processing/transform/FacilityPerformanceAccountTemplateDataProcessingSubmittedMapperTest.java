package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataProcessingSubmittedMapperTest {

    private final FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper mapper =
            Mappers.getMapper(FacilityPerformanceAccountTemplateDataProcessingSubmittedMapper.class);

    @Test
    void toFacilityPerformanceAccountTemplateDataFacilitySubmittedRequestActionPayload() {
        final Long facilityId = 11L;
        final String parentRequestId = "parentRequestId";
        final Year targetYear = Year.of(2026);
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder().id(22L).build();
        final FacilityDTO facility = FacilityDTO.builder().id(facilityId).facilityBusinessId("facilityBusinessId").build();
        final int reportVersion = 1;
        final LocalDateTime submissionDate = LocalDate.of(2026, 2, 2).atStartOfDay();

        final List<FacilityPerformanceAccountTemplateSavingAction> savingActions = List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
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
                .build());

        final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload =
                FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_PAYLOAD)
                        .sectorUserAssignee("sectorUserAssignee")
                        .parentRequestId(parentRequestId)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .targetYear(targetYear)
                        .submissionDate(submissionDate)
                        .facility(facility)
                        .build();
        final FacilityPerformanceAccountTemplateDataContainer container = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(savingActions)
                .build();

        final FacilityPerformanceAccountTemplateProcessingResults processingResults = FacilityPerformanceAccountTemplateProcessingResults.builder()
                .container(container)
                .reportVersion(reportVersion)
                .build();

        FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload expected = FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD)
                .performanceData(FacilityPerformanceAccountTemplateDataContainer.builder()
                        .savingActions(savingActions)
                        .build())
                .targetPeriodYear(targetYear)
                .build();

        // invoke
        FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload result =
                mapper.toFacilityPerformanceAccountTemplateDataFacilitySubmittedRequestActionPayload(requestPayload, processingResults);


        // verify
        assertThat(result).isEqualTo(expected);
    }
}
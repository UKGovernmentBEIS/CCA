package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceTemplateDataUploadMapperTest {

    private final FacilityPerformanceTemplateDataUploadMapper mapper = Mappers.getMapper(FacilityPerformanceTemplateDataUploadMapper.class);

    @Test
    void toSavingAction() {
        final Year targetPeriodYear = Year.of(2026);
        final LocalDate implementationDate = LocalDate.of(2026, Month.APRIL, 1);
        final BigDecimal energyConsumptionOrCarbonEmissionsImpactedPercentage = new BigDecimal(10).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedExtentOfChangeImplementedPercentage = new BigDecimal(25).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal expectedSavingsFromTheChangeImplementedPercentage = new BigDecimal(60).setScale(7, RoundingMode.HALF_DOWN);
        final BigDecimal estimatedChangeInEnergyConsumptionPercentage = new BigDecimal("1.125").setScale(7, RoundingMode.HALF_DOWN);
        final FacilityPerformanceAccountTemplateDataUploadCsvData csvRowData = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename("filename")
                .rowNumber(1)
                .facilityBusinessId("facilityBusinessId")
                .actionCategoryType(ActionCategoryType.PROCESS_OPTIMISATION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("Process improvements")
                .reasonsForImplementation("Tweaked a few settings to make the process more efficient")
                .implementationDate(implementationDate)
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(energyConsumptionOrCarbonEmissionsImpactedPercentage)
                .expectedExtentOfChangeImplementedPercentage(expectedExtentOfChangeImplementedPercentage)
                .expectedSavingsFromTheChangeImplementedPercentage(expectedSavingsFromTheChangeImplementedPercentage)
                .notes("We could only implement this measure half way through TP6")
                .build();

        final FacilityPerformanceAccountTemplateSavingAction expected = FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.PROCESS_OPTIMISATION)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("Process improvements")
                .reasonsForImplementation("Tweaked a few settings to make the process more efficient")
                .implementationDate(implementationDate)
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(energyConsumptionOrCarbonEmissionsImpactedPercentage)
                .expectedExtentOfChangeImplementedPercentage(expectedExtentOfChangeImplementedPercentage)
                .expectedSavingsFromTheChangeImplementedPercentage(expectedSavingsFromTheChangeImplementedPercentage)
                .estimatedChangeInEnergyConsumptionPercentage(estimatedChangeInEnergyConsumptionPercentage)
                .notes("We could only implement this measure half way through TP6")
                .build();

        FacilityPerformanceAccountTemplateSavingAction actual = mapper.toSavingAction(csvRowData, targetPeriodYear);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

}

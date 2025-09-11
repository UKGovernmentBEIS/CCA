package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceAccountTemplateReportData {
    private String targetUnitAccountBusinessId;
    private String operatorName;
    private String targetType;
    private String targetPercentage;
    private String improvementAchievedPercentage;
    private String improvementAccountedPercentage;
    private String performanceImpactedByAnyImplementedMeasures;
    private String performanceImpactedByAnyImplementedMeasuresSupportingText;
    private int totalRowIndex;
    private String totalEstimateChangeInEnergyConsumptionPercentage;
    private String totalEstimateChangeInCarbonEmissionsPercentage;
    @Builder.Default
    private List<EnergyOrCarbonSavingActionsAndMeasuresImplementedRow> energyOrCarbonSavingActionsAndMeasuresImplemented = new ArrayList<>();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EnergyOrCarbonSavingActionsAndMeasuresImplementedRow {
        private int excelRowIndex;
        private String facilityId;
        private String actionCategoryType;
        private String savingActionsImplemented;
        private String reasonsForImplementation;
        private String implementationDate;
        private String fixedEnergyConsumptionOrCarbonEmissionsImpacted;
        private String energyConsumptionOrCarbonEmissionsImpactedPercentage;
        private String expectedExtentOfChangeImplementedPercentage;
        private String expectedSavingsFromTheChangeImplementedPercentage;
        private String estimatedChangeInEnergyConsumptionPercentage;
        private String estimatedChangeInCarbonEmissionsPercentage;
        private String notes;
    }
}

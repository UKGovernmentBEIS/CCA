package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedItem;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {
        ActionCategoryType.class,
        EnergyConsumptionOrCarbonEmissionsImpactedType.class,
        TargetType.class
})
public interface PerformanceAccountTemplateDataContainerMapper {

    @Mapping(target = "targetUnitIdentityAndPerformance.targetType",
            expression = "java(TargetType.fromDescription(performanceAccountTemplateReportData.getTargetType()))")
    @Mapping(target = "targetUnitIdentityAndPerformance.targetPercentage",
            source = "data.targetPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "targetUnitIdentityAndPerformance.improvementAchievedPercentage",
            source = "data.improvementAchievedPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "targetUnitIdentityAndPerformance.improvementAccountedPercentage",
            source = "data.improvementAccountedPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "targetUnitIdentityAndPerformance.performanceImpactedByAnyImplementedMeasures",
            source = "data.performanceImpactedByAnyImplementedMeasures")
    @Mapping(target = "targetUnitIdentityAndPerformance.performanceImpactedByAnyImplementedMeasuresSupportingText",
            source = "data.performanceImpactedByAnyImplementedMeasuresSupportingText")
    @Mapping(target = "energyOrCarbonSavingActionsAndMeasuresImplementedItems",
            source = "data.energyOrCarbonSavingActionsAndMeasuresImplemented")
    @Mapping(target = "targetUnitIdentityAndPerformance.totalEstimateChangeInEnergyConsumptionPercentage",
            source = "data.totalEstimateChangeInEnergyConsumptionPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "targetUnitIdentityAndPerformance.totalEstimateChangeInCarbonEmissionsPercentage",
            source = "data.totalEstimateChangeInCarbonEmissionsPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "file", source = "file")
    PerformanceAccountTemplateDataContainer toPerformanceAccountTemplateDataContainer(PerformanceAccountTemplateReportData data, FileInfoDTO file);

    @Mapping(target = "facilityId", source = "facilityId")
    @Mapping(target = "actionCategoryType",
            expression = "java(ActionCategoryType.fromDescription(row.getActionCategoryType()))")
    @Mapping(target = "savingActionsImplemented", source = "savingActionsImplemented")
    @Mapping(target = "reasonsForImplementation", source = "reasonsForImplementation")
    @Mapping(target = "fixedEnergyConsumptionOrCarbonEmissionsImpacted",
            expression = "java(EnergyConsumptionOrCarbonEmissionsImpactedType.fromDescription(row.getFixedEnergyConsumptionOrCarbonEmissionsImpacted()))")
    @Mapping(target = "energyConsumptionOrCarbonEmissionsImpactedPercentage",
            source = "energyConsumptionOrCarbonEmissionsImpactedPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "expectedExtentOfChangeImplementedPercentage",
            source = "expectedExtentOfChangeImplementedPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "expectedSavingsFromTheChangeImplementedPercentage",
            source = "expectedSavingsFromTheChangeImplementedPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "estimatedChangeInEnergyConsumptionPercentage",
            source = "estimatedChangeInEnergyConsumptionPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "estimatedChangeInCarbonEmissionsPercentage",
            source = "estimatedChangeInCarbonEmissionsPercentage",
            qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "notes", source = "notes")
    EnergyOrCarbonSavingActionsAndMeasuresImplementedItem toEnergyOrCarbonSavingActionsAndMeasuresImplementedItem(
            EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row
    );

    @Named("stringToBigDecimal")
    default BigDecimal stringToBigDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value).setScale(20, RoundingMode.DOWN);
    }
}

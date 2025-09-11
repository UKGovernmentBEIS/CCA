package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.EnergyOrCarbonSavingActionsAndMeasuresImplementedItem;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData.EnergyOrCarbonSavingActionsAndMeasuresImplementedRow;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;

class PerformanceAccountTemplateDataContainerMapperTest {
    
    private final PerformanceAccountTemplateDataContainerMapper mapper =
            Mappers.getMapper(PerformanceAccountTemplateDataContainerMapper.class);
    
    @Test
    void testToPerformanceAccountTemplateDataContainer_simpleMapping() {
        PerformanceAccountTemplateReportData inputData = getPerformanceAccountTemplateReportData();
        
        FileInfoDTO fileInfo = new FileInfoDTO("test-file.xlsx", "uuid");
        
        PerformanceAccountTemplateDataContainer result =
                mapper.toPerformanceAccountTemplateDataContainer(inputData, fileInfo);
        
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getTargetUnitIdentityAndPerformance());
        
        Assertions.assertNotNull(result.getTargetUnitIdentityAndPerformance().getTargetType());
        Assertions.assertEquals(
                new BigDecimal("12.345").setScale(20, BigDecimal.ROUND_DOWN),
                result.getTargetUnitIdentityAndPerformance().getTargetPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("0.001").setScale(20, BigDecimal.ROUND_DOWN),
                result.getTargetUnitIdentityAndPerformance().getImprovementAchievedPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("3.333").setScale(20, BigDecimal.ROUND_DOWN),
                result.getTargetUnitIdentityAndPerformance().getImprovementAccountedPercentage()
        );
        
        Assertions.assertEquals("Yes", 
                result.getTargetUnitIdentityAndPerformance().getPerformanceImpactedByAnyImplementedMeasures()
        );
        Assertions.assertEquals("Yes, details...",
                result.getTargetUnitIdentityAndPerformance().getPerformanceImpactedByAnyImplementedMeasuresSupportingText());
        Assertions.assertEquals(
                new BigDecimal("0.015").setScale(20, BigDecimal.ROUND_DOWN),
                result.getTargetUnitIdentityAndPerformance().getTotalEstimateChangeInEnergyConsumptionPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("0.123").setScale(20, BigDecimal.ROUND_DOWN),
                result.getTargetUnitIdentityAndPerformance().getTotalEstimateChangeInCarbonEmissionsPercentage()
        );
        
        
        Assertions.assertNotNull(result.getFile());
        Assertions.assertEquals("test-file.xlsx", result.getFile().getName());
        Assertions.assertEquals("uuid", result.getFile().getUuid());
        
        Assertions.assertNotNull(result.getEnergyOrCarbonSavingActionsAndMeasuresImplementedItems());
        Assertions.assertEquals(1, result.getEnergyOrCarbonSavingActionsAndMeasuresImplementedItems().size());
        EnergyOrCarbonSavingActionsAndMeasuresImplementedItem item =
                result.getEnergyOrCarbonSavingActionsAndMeasuresImplementedItems().get(0);
        
        Assertions.assertEquals("FAC-001", item.getFacilityId());
        Assertions.assertEquals("Replaced old bulbs with LED", item.getSavingActionsImplemented());
        Assertions.assertEquals("Cost savings & compliance", item.getReasonsForImplementation());
        Assertions.assertEquals("2025-01-15", item.getImplementationDate());
        Assertions.assertNotNull(item.getActionCategoryType());
        Assertions.assertNotNull(item.getFixedEnergyConsumptionOrCarbonEmissionsImpacted());
        
        Assertions.assertEquals(
                new BigDecimal("10").setScale(20, BigDecimal.ROUND_DOWN),
                item.getEnergyConsumptionOrCarbonEmissionsImpactedPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("5").setScale(20, BigDecimal.ROUND_DOWN),
                item.getExpectedExtentOfChangeImplementedPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("2.5").setScale(20, BigDecimal.ROUND_DOWN),
                item.getExpectedSavingsFromTheChangeImplementedPercentage()
        );
        Assertions.assertEquals(
                new BigDecimal("1.234").setScale(20, BigDecimal.ROUND_DOWN),
                item.getEstimatedChangeInEnergyConsumptionPercentage()
        );
        Assertions.assertEquals("Additional notes here", item.getNotes());
    }
    
    @Test
    void testStringToBigDecimal_withBlankValue() {
        BigDecimal result = mapper.stringToBigDecimal("");
        Assertions.assertNull(result);
    }
    
    @Test
    void testStringToBigDecimal_withInvalidValue() {
        Assertions.assertThrows(NumberFormatException.class,
                () -> mapper.stringToBigDecimal("not-a-number"));
    }
    
    private static @NotNull PerformanceAccountTemplateReportData getPerformanceAccountTemplateReportData() {
        PerformanceAccountTemplateReportData inputData = new PerformanceAccountTemplateReportData();
        inputData.setTargetUnitAccountBusinessId("businessId-123");
        inputData.setOperatorName("Test Operator");
        inputData.setTargetType("Novem Energy");
        inputData.setTargetPercentage("12.345");
        inputData.setImprovementAchievedPercentage("0.001");
        inputData.setImprovementAccountedPercentage("3.333");
        inputData.setPerformanceImpactedByAnyImplementedMeasures("Yes");
        inputData.setPerformanceImpactedByAnyImplementedMeasuresSupportingText("Yes, details...");
        inputData.setTotalEstimateChangeInEnergyConsumptionPercentage("0.015");
        inputData.setTotalEstimateChangeInCarbonEmissionsPercentage("0.123");
        
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row = getEnergyOrCarbonSavingActionsAndMeasuresImplementedRow();
        
        inputData.setEnergyOrCarbonSavingActionsAndMeasuresImplemented(Collections.singletonList(row));
        return inputData;
    }
    
    private static @NotNull EnergyOrCarbonSavingActionsAndMeasuresImplementedRow getEnergyOrCarbonSavingActionsAndMeasuresImplementedRow() {
        EnergyOrCarbonSavingActionsAndMeasuresImplementedRow row =
                new EnergyOrCarbonSavingActionsAndMeasuresImplementedRow();
        row.setFacilityId("FAC-001");
        row.setActionCategoryType("Energy Management");
        row.setSavingActionsImplemented("Replaced old bulbs with LED");
        row.setReasonsForImplementation("Cost savings & compliance");
        row.setImplementationDate("2025-01-15");
        row.setFixedEnergyConsumptionOrCarbonEmissionsImpacted("Fixed");
        row.setEnergyConsumptionOrCarbonEmissionsImpactedPercentage("10");
        row.setExpectedExtentOfChangeImplementedPercentage("5");
        row.setExpectedSavingsFromTheChangeImplementedPercentage("2.5");
        row.setEstimatedChangeInEnergyConsumptionPercentage("1.234");
        row.setNotes("Additional notes here");
        return row;
    }
}


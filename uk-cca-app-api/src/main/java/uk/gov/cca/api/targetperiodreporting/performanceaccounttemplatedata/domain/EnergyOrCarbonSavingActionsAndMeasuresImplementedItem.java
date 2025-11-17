package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyOrCarbonSavingActionsAndMeasuresImplementedItem {
	
	// The facility business id
    private String facilityId;
    
    @NotNull
    private ActionCategoryType actionCategoryType;
    
    @NotNull
    private String savingActionsImplemented;
    
    @NotNull
    private String reasonsForImplementation;
    
    @NotNull
    private String implementationDate;
    
    @NotNull
    private EnergyConsumptionOrCarbonEmissionsImpactedType fixedEnergyConsumptionOrCarbonEmissionsImpacted;
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal energyConsumptionOrCarbonEmissionsImpactedPercentage;
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal expectedExtentOfChangeImplementedPercentage;
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal expectedSavingsFromTheChangeImplementedPercentage;
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal estimatedChangeInEnergyConsumptionPercentage;
    
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal estimatedChangeInCarbonEmissionsPercentage;
    
    private String notes;
}

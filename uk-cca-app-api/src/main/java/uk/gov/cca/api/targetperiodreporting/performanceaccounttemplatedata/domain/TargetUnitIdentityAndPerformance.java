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
public class TargetUnitIdentityAndPerformance {
    
    private TargetType targetType;
    
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal targetPercentage;
   
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal improvementAchievedPercentage;
    
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal improvementAccountedPercentage;
    
    @NotNull
    private String performanceImpactedByAnyImplementedMeasures;
    
    private String performanceImpactedByAnyImplementedMeasuresSupportingText;
    
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal totalEstimateChangeInEnergyConsumptionPercentage;
    
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal totalEstimateChangeInCarbonEmissionsPercentage;
}

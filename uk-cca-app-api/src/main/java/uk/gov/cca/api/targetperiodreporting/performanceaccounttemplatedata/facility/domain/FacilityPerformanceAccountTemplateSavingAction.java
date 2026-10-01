package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#actionCategoryType == null) " +
        "|| ( (#actionCategoryType ne 'NO_ACTION') && (#supplyDemandSideMeasure != null) && (#savingActionsImplemented != null) " +
        "&& (#reasonsForImplementation != null) && (#implementationDate != null) && (#fixedEnergyConsumptionOrCarbonEmissionsImpacted != null) " +
        "&& (#energyConsumptionOrCarbonEmissionsImpactedPercentage != null) && (#expectedExtentOfChangeImplementedPercentage != null) && (#expectedSavingsFromTheChangeImplementedPercentage != null) )" +
        "|| ( (#actionCategoryType eq 'NO_ACTION') && (#supplyDemandSideMeasure == null) && (#savingActionsImplemented == null) " +
        "&& (#reasonsForImplementation == null) && (#implementationDate == null) && (#fixedEnergyConsumptionOrCarbonEmissionsImpacted == null) " +
        "&& (#energyConsumptionOrCarbonEmissionsImpactedPercentage == null) && (#expectedExtentOfChangeImplementedPercentage == null) && (#expectedSavingsFromTheChangeImplementedPercentage == null) && (#notes != null) )}",
        message = "performanceaccounttemplatedata.facility.facilityPerformanceAccountTemplateSavingAction.mandatoryFieldsIncomplete")
public class FacilityPerformanceAccountTemplateSavingAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private ActionCategoryType actionCategoryType;

    private SupplyDemandSideMeasure supplyDemandSideMeasure;

    private String savingActionsImplemented;

    private String reasonsForImplementation;

    private LocalDate implementationDate;

    private EnergyConsumptionOrCarbonEmissionsImpactedType fixedEnergyConsumptionOrCarbonEmissionsImpacted;

    @DecimalMax(value = "100")
    @DecimalMin(value = "0")
    @Digits(integer = 2, fraction = 7)
    private BigDecimal energyConsumptionOrCarbonEmissionsImpactedPercentage;

    @DecimalMax(value = "100")
    @DecimalMin(value = "0")
    @Digits(integer = 2, fraction = 7)
    private BigDecimal expectedExtentOfChangeImplementedPercentage;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal expectedSavingsFromTheChangeImplementedPercentage;

    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal estimatedChangeInEnergyConsumptionPercentage;

    private String notes;
}

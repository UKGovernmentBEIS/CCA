package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.validation;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.validation.FunctionalValidation;
import uk.gov.cca.api.common.validation.FunctionalValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;

import java.math.BigDecimal;
import java.util.function.Predicate;

@UtilityClass
public class PerformanceDataFacilityValidationHelper {

    public final String FACILITY_ERROR_MESSAGE = "%s [facility: %s] %s";

    public FunctionalValidator<PerformanceDataFacilityThroughputDetails> validate(Predicate<PerformanceDataFacilityThroughputDetails> predicate, String errorMessage) {
        return FunctionalValidation.from(predicate, errorMessage);
    }

    public FunctionalValidator<PerformanceDataFacilityInputEnergyFuelDetails> validateSRM(String errorMessage) {
        return FunctionalValidation.from(fuels ->
                        ObjectUtils.isNotEmpty(fuels.getElectricitySuppliedFromCHP()) && ObjectUtils.isNotEmpty(fuels.getThroughputAdjustmentFactor()),
                errorMessage);
    }

    public FunctionalValidator<PerformanceDataFacilityInputEnergyFuelDetails> validateCHP(String errorMessage) {
        return FunctionalValidation.from(fuels ->
                        ObjectUtils.isEmpty(fuels.getElectricitySuppliedFromCHP())
                        || fuels.getElectricitySuppliedFromCHP().compareTo(BigDecimal.ZERO) == 0
                        || (fuels.getElectricitySuppliedFromCHP().compareTo(BigDecimal.ZERO) > 0
                                && (fuels.getStandardFuels().containsKey(PerformanceDataFacilityFixedConversionFactor.GRID_ELECTRICITY)
                                    || fuels.getStandardFuels().containsKey(PerformanceDataFacilityFixedConversionFactor.NON_GRID_ELECTRICITY))),
                errorMessage);
    }

    public FunctionalValidator<PerformanceDataFacilityInputEnergyFuelDetails> validateNotSRM(String errorMessage) {
        return FunctionalValidation.from(fuels ->
                        ObjectUtils.isEmpty(fuels.getElectricitySuppliedFromCHP()) && ObjectUtils.isEmpty(fuels.getThroughputAdjustmentFactor()),
                errorMessage);
    }
}

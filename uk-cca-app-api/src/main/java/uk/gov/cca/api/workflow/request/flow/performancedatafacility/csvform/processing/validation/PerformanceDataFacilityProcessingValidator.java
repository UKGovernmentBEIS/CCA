package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityUploadCsvData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PerformanceDataFacilityProcessingValidator {

    public BusinessValidationResult validateCsvRules(final PerformanceDataFacilityUploadCsvData csvData,
                                                     final PerformanceDataFacilityCalculationParameters calculationParameters) {
        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        // At least one of the fuels (standard or non-standard) MUST have a value > zero (0)
        BigDecimal sumOfFuels = Stream.of(csvData.getGridElectricity(), csvData.getNonGridElectricity(), csvData.getNaturalGas(), csvData.getLpg(),
                csvData.getGasDieselOil(), csvData.getKerosene(), csvData.getFuelOil(), csvData.getCoal(),
                csvData.getCoke(), csvData.getPetrol(), csvData.getNitrogen(), csvData.getCarbonDioxide(),
                csvData.getEthane(), csvData.getNaphtha(), csvData.getPetroleumCoke(), csvData.getRefineryGas(),
                csvData.getOtherFuelAmount1(), csvData.getOtherFuelAmount2(), csvData.getOtherFuelAmount3(),
                csvData.getOtherFuelAmount4(), csvData.getOtherFuelAmount5(), csvData.getOtherFuelAmount6(),
                csvData.getOtherFuelAmount7(), csvData.getOtherFuelAmount8(), csvData.getOtherFuelAmount9(),
                csvData.getOtherFuelAmount10()
        ).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        if(sumOfFuels.compareTo(BigDecimal.ZERO) == 0) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation
                    .PerformanceDataFacilityViolationMessage.FACILITY_CSV_FUELS_NOT_VALID));
        }

        // If the facility uses Split by product for variable energy, the name and throughput for at least one product must be provided
        if(calculationParameters.getVariableEnergyType() == VariableEnergyDepictionType.BY_PRODUCT) {
            List<BigDecimal> products = Stream.of(csvData.getProductActualThroughput1(), csvData.getProductActualThroughput2(), csvData.getProductActualThroughput3(),
                    csvData.getProductActualThroughput4(), csvData.getProductActualThroughput5(), csvData.getProductActualThroughput6(),
                    csvData.getProductActualThroughput7(), csvData.getProductActualThroughput8(), csvData.getProductActualThroughput9(),
                    csvData.getProductActualThroughput10(), csvData.getProductActualThroughput11(), csvData.getProductActualThroughput12(),
                    csvData.getProductActualThroughput13(), csvData.getProductActualThroughput14(), csvData.getProductActualThroughput15(),
                    csvData.getProductActualThroughput16(), csvData.getProductActualThroughput17(), csvData.getProductActualThroughput18(),
                    csvData.getProductActualThroughput19(), csvData.getProductActualThroughput20()
            ).filter(Objects::nonNull).toList();

            if(products.isEmpty()) {
                violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation
                        .PerformanceDataFacilityViolationMessage.FACILITY_CSV_PRODUCTS_NOT_VALID));
            }
        }


        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations).build();
    }
}

package uk.gov.cca.api.workflow.request.flow.common.domain;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;

import java.util.List;
import java.util.Map;

public interface PerformanceEnergyFuelDetails {

    Map<PerformanceDataFacilityFixedConversionFactor, PerformanceDataFacilityFuelEnergyConsumption> getStandardFuels();

    List<PerformanceDataFacilityNonStandardFuel> getNonStandardFuels();
}

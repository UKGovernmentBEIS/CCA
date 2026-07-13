package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputEnergyFuelDetails;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private PerformanceDataFacilityInputEnergyFuelDetails energyFuelDetails;

    private PerformanceDataFacilityThroughputDetails throughputDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}

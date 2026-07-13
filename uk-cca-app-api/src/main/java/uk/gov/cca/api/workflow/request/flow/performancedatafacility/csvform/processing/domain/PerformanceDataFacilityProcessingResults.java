package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityProcessingResults {

    private PerformanceDataFacilityContainer container;
    private int reportVersion;
}

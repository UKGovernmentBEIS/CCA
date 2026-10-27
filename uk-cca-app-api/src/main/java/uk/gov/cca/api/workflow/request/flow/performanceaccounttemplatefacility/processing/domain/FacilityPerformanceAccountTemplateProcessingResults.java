package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityPerformanceAccountTemplateProcessingResults {

    private int reportVersion;
    private FacilityPerformanceAccountTemplateDataContainer container;
}

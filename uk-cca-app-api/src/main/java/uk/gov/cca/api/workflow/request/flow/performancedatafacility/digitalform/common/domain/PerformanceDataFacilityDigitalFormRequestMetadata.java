package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityRequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDigitalFormRequestMetadata extends PerformanceDataFacilityRequestMetadata {
}

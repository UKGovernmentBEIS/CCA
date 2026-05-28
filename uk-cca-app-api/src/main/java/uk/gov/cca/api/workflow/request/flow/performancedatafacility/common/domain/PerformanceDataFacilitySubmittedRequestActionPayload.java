package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilitySubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private PerformanceDataFacilitySubmissionDetails details;

    @NotNull
    @Valid
    private PerformanceDataFacilityContainer performanceData;
}

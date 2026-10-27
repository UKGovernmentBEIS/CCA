package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    private Year targetPeriodYear;

    @NotNull
    @Valid
    private FacilityPerformanceAccountTemplateDataContainer performanceData;
}
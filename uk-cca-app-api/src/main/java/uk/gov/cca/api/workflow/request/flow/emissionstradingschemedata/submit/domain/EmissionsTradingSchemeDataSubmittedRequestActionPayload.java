package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionsTradingSchemeDataSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private EmissionsTradingSchemeDataSubmissionDetails details;

    @NotNull
    @Valid
    private EmissionsTradingSchemeDataContainer performanceData;
}

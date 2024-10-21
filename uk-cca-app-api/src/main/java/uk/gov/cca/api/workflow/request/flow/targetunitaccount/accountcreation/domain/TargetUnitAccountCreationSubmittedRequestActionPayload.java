package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitAccountCreationSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @Valid
    @NotNull
    private TargetUnitAccountPayload payload;
}

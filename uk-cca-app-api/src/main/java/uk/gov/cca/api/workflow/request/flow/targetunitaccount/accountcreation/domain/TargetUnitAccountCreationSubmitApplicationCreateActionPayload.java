package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitAccountCreationSubmitApplicationCreateActionPayload extends RequestCreateActionPayload {

    @Valid
    @NotNull
    private TargetUnitAccountPayload payload;
}

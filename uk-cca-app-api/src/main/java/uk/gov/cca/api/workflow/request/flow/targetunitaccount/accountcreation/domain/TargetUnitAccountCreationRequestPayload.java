package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitAccountCreationRequestPayload extends CcaRequestPayload {

    private TargetUnitAccountPayload payload;
}

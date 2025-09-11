package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload extends CcaNotifyOperatorForDecisionRequestTaskActionPayload {

    @NotNull
    @Valid
    private UnderlyingAgreementVariationPayload underlyingAgreementProposed;
}

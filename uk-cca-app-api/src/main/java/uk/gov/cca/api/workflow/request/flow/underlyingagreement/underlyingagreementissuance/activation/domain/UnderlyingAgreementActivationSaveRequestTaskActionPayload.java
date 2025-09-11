package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementActivationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

	private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}

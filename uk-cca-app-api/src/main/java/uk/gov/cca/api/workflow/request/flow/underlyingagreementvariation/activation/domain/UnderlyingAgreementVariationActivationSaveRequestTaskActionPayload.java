package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}

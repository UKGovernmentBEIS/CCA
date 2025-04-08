package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationActivatedRequestActionPayload extends UnderlyingAgreementVariationAcceptedRequestActionPayload {

    @NotNull
    @Valid
    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    @Builder.Default
    private Map<UUID, String> underlyingAgreementActivationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), this.getUnderlyingAgreementActivationAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

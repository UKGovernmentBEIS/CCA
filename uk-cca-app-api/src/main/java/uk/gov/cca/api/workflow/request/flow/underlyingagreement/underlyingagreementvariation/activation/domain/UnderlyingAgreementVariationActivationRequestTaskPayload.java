package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationActivationRequestTaskPayload extends RequestTaskPayload {

    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> underlyingAgreementActivationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getUnderlyingAgreementActivationAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Optional.ofNullable(getUnderlyingAgreementActivationDetails())
                .map(UnderlyingAgreementActivationDetails::getEvidenceFiles)
                .orElse(Collections.emptySet());
    }
}

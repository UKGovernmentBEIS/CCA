package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestTaskPayload;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationSubmitRequestTaskPayload extends UnderlyingAgreementVariationRequestTaskPayload {

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        /* As the UnA variation review can be partially approved, we need to retain the original underlying attachment IDs.
        If a task section containing attachments is rejected during the review, we must be able to locate the section’s original
        attachments in the referenced attachments ids so that the validations can pass */
        return getUnderlyingAgreement() != null ?
                Stream.concat(getUnderlyingAgreement().getUnderlyingAgreement().getUnderlyingAgreementSectionAttachmentIds().stream(),
                                getOriginalUnderlyingAgreementContainer().getUnderlyingAgreementAttachments().keySet().stream())
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
    }
}

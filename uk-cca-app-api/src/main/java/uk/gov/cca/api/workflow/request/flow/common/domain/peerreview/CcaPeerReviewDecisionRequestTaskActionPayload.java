package uk.gov.cca.api.workflow.request.flow.common.domain.peerreview;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CcaPeerReviewDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @Valid
    private CcaPeerReviewDecision decision;

    public Set<UUID> getReferencedAttachmentIds() {
        return Optional.ofNullable(getDecision().getFiles())
                .orElse(Collections.emptySet());
    }
}

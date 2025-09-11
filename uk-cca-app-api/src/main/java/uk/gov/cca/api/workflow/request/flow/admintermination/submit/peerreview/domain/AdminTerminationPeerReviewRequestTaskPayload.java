package uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskPayload;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminTerminationPeerReviewRequestTaskPayload extends AdminTerminationSubmitRequestTaskPayload implements CcaPeerReviewDecisionRequestTaskPayload {

    private CcaPeerReviewDecision decision;

    @Builder.Default
    private Map<UUID, String> peerReviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), this.getPeerReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        Set<UUID> peerReviewAttachmentIds = Optional.ofNullable(getDecision())
                .map(CcaPeerReviewDecision::getFiles)
                .orElse(Collections.emptySet());

        return Stream.of(super.getReferencedAttachmentIds(), peerReviewAttachmentIds)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getPeerReviewAttachments().keySet().removeIf(uuids::contains);
    }
}

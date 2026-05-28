package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceAppealOutcomeSubmitRequestTaskPayload extends RequestTaskPayload implements NonComplianceRequestTaskAttachable {

    private NonComplianceAppealOutcomeDetails appealOutcome;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getNonComplianceAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Optional.ofNullable(this.appealOutcome)
                .map(n -> n.getFile() != null ? Set.of(n.getFile()) : new HashSet<UUID>())
                .orElseGet(Collections::emptySet);
    }
}

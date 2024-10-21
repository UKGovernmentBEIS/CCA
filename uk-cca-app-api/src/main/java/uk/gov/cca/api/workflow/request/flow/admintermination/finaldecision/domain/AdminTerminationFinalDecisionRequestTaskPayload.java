package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestTaskAttachable;
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
public class AdminTerminationFinalDecisionRequestTaskPayload extends RequestTaskPayload implements AdminTerminationRequestTaskAttachable {

    private AdminTerminationFinalDecisionReasonDetails adminTerminationFinalDecisionReasonDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> adminTerminationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getAdminTerminationAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Optional.ofNullable(getAdminTerminationFinalDecisionReasonDetails())
                .map(AdminTerminationFinalDecisionReasonDetails::getRelevantFiles)
                .orElse(Collections.emptySet());
    }
}

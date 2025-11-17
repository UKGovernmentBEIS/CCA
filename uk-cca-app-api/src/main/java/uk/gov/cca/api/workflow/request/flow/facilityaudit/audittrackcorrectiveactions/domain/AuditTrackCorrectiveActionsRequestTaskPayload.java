package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditTrackCorrectiveActionsRequestTaskPayload extends RequestTaskPayload implements FacilityAuditRequestTaskAttachable {

    @NotNull
    @Valid
    private AuditTrackCorrectiveActions auditTrackCorrectiveActions;

    @Builder.Default
    private Set<String> respondedActions = new HashSet<>();

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> facilityAuditAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getFacilityAuditAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        if (this.auditTrackCorrectiveActions == null) {
            return Collections.emptySet();
        }
        return this.auditTrackCorrectiveActions.getCorrectiveActionResponses()
                .values()
                .stream()
                .flatMap(ca -> ca.getResponse().getEvidenceFiles().stream())
                .collect(Collectors.toSet());
    }
}

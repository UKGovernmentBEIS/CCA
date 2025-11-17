package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain;

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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditDetailsCorrectiveActionsSubmitRequestTaskPayload extends RequestTaskPayload implements FacilityAuditRequestTaskAttachable {

    @NotNull
    @Valid
    private AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions;

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
        if (this.auditDetailsAndCorrectiveActions == null) {
            return Collections.emptySet();
        }
        return Optional.ofNullable(this.auditDetailsAndCorrectiveActions.getAuditDetails())
                .map(AuditDetails::getAuditDocuments)
                .orElse(Collections.emptySet());
    }

}

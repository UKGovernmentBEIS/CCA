package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAppealable;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskClosable;
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
public class NonComplianceConclusionSubmitRequestTaskPayload extends RequestTaskPayload implements NonComplianceRequestTaskClosable, NonComplianceRequestTaskAppealable {

    private NonComplianceConclusion nonComplianceConclusion;

    private NonComplianceAppealDetails appealDetails;

    private NonComplianceCloseJustification closeJustification;

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
        if (this.closeJustification != null) {
            return this.closeJustification.getFiles();
        } else if (this.appealDetails != null) {
            return this.appealDetails.getFiles();
        } else {
            return Optional.ofNullable(this.nonComplianceConclusion.getWithdrawNotice())
                    .map(n -> n.getFile() != null ? Set.of(n.getFile()) : new HashSet<UUID>())
                    .orElseGet(Collections::emptySet);
        }
    }
}

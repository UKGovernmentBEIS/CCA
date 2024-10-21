package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestTaskAttachable;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminTerminationWithdrawRequestTaskPayload extends RequestTaskPayload implements AdminTerminationRequestTaskAttachable {

    private AdminTerminationWithdrawReasonDetails adminTerminationWithdrawReasonDetails;

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
        return Optional.ofNullable(getAdminTerminationWithdrawReasonDetails())
                .map(AdminTerminationWithdrawReasonDetails::getRelevantFiles)
                .orElse(Collections.emptySet());
    }

}

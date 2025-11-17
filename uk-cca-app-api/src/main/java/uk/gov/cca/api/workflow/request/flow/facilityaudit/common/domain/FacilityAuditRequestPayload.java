package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityAuditRequestPayload extends CcaRequestPayload {

    private PreAuditReviewDetails preAuditReviewDetails;

    private AuditDetailsAndCorrectiveActions auditDetailsAndCorrectiveActions;

    private AuditTrackCorrectiveActions auditTrackCorrectiveActions;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> preAuditReviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> auditDetailsCorrectiveActionsAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> auditTrackCorrectiveActionsAttachments = new HashMap<>();
}

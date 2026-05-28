package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntent;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceRequestPayload extends CcaRequestPayload {

    private NonComplianceDetails nonComplianceDetails;

    private NonComplianceNoticeOfIntent noticeOfIntent;

    private NonComplianceEnforcementResponseNotice enforcementResponseNotice;

    private NonComplianceConclusion nonComplianceConclusion;

    private NonComplianceAppealDetails appealDetails;

    private NonComplianceAppealOutcomeDetails appealOutcome;

    private NonComplianceCloseJustification closeJustification;

    private DecisionNotification decisionNotification;

    private boolean penaltyReissueNeeded;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();
}

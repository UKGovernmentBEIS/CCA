package uk.gov.cca.api.workflow.request.flow.admintermination.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementVersion;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminTerminationRequestPayload extends CcaRequestPayload implements UnderlyingAgreementVersion {

    private int underlyingAgreementVersion;

    private AdminTerminationReasonDetails adminTerminationReasonDetails;

    private AdminTerminationWithdrawReasonDetails adminTerminationWithdrawReasonDetails;

    private AdminTerminationFinalDecisionReasonDetails adminTerminationFinalDecisionReasonDetails;

    private LocalDateTime submitSubmissionDate;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> adminTerminationSubmitAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> adminTerminationWithdrawAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> adminTerminationFinalDecisionAttachments = new HashMap<>();

    private CcaDecisionNotification decisionNotification;

    private FileInfoDTO officialNotice;
}

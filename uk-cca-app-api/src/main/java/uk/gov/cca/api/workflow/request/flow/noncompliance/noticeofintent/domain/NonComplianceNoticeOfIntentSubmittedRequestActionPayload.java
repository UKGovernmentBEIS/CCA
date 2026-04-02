package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceNoticeOfIntentSubmittedRequestActionPayload extends CcaRequestActionPayload {

    private NoticeOfIntent noticeOfIntent;

    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Valid
    @NotEmpty
    private List<DefaultNoticeRecipient> defaultContacts;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }
}

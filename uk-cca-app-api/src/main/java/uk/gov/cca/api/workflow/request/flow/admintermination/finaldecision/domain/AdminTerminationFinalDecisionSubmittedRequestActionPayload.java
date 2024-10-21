package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdminTerminationFinalDecisionSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private AdminTerminationFinalDecisionReasonDetails adminTerminationFinalDecisionReasonDetails;

    @NotNull
    @Valid
    private CcaDecisionNotification decisionNotification;

    @Builder.Default
    private Map<UUID, String> adminTerminationFinalDecisionAttachments = new HashMap<>();

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Valid
    @NotEmpty
    private List<DefaultNoticeRecipient> defaultContacts;

    @NotNull
    private FileInfoDTO officialNotice;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getAdminTerminationFinalDecisionAttachments();
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(
                super.getFileDocuments(),
                Map.of(UUID.fromString(officialNotice.getUuid()), officialNotice.getName())
        ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

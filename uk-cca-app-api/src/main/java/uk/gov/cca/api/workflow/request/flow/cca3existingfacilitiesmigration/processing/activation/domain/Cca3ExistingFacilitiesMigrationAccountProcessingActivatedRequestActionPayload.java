package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain;

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

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails activationDetails;

    @Builder.Default
    private Map<UUID, String> activationAttachments = new HashMap<>();

    @NotNull
    @Valid
    private CcaDecisionNotification decisionNotification;

    @Valid
    @NotEmpty
    private List<DefaultNoticeRecipient> defaultContacts;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @NotNull
    @Valid
    private FileInfoDTO officialNotice;

    @NotNull
    @Valid
    private FileInfoDTO underlyingAgreementDocument;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getActivationAttachments();
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Map.of(
                UUID.fromString(underlyingAgreementDocument.getUuid()), underlyingAgreementDocument.getName(),
                UUID.fromString(officialNotice.getUuid()), officialNotice.getName()
        );
    }
}

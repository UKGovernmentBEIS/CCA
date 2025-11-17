package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    private FileInfoDTO officialNotice;

    @NotNull
    private FileInfoDTO underlyingAgreementDocument;

    @NotEmpty
    private List<@Valid @NotNull DefaultNoticeRecipient> defaultContacts;

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Map.of(
                UUID.fromString(underlyingAgreementDocument.getUuid()), underlyingAgreementDocument.getName(),
                UUID.fromString(officialNotice.getUuid()), officialNotice.getName()
        );
    }
}

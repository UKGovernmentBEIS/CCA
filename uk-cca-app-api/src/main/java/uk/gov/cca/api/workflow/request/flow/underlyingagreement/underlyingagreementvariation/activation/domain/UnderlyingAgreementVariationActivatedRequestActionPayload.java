package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain;

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
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
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
public class UnderlyingAgreementVariationActivatedRequestActionPayload extends CcaRequestActionPayload {

    private AccountReferenceData accountReferenceData;

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

    @NotEmpty
    private List<FileInfoDTO> officialNotices;

    @NotEmpty
    private Map<SchemeVersion, FileInfoDTO> underlyingAgreementDocuments;

    @NotNull
    @Valid
    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    @Builder.Default
    private Map<UUID, String> underlyingAgreementActivationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getUnderlyingAgreementActivationAttachments();
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        Map<UUID, String> notices = ObjectUtils.isEmpty(this.officialNotices)
                ? Map.of()
                : this.officialNotices.stream()
                    .collect(Collectors.toMap(f -> UUID.fromString(f.getUuid()), FileInfoDTO::getName));

        Map<UUID, String> documents = ObjectUtils.isEmpty(this.underlyingAgreementDocuments)
                ? Map.of()
                : underlyingAgreementDocuments.values().stream()
                    .map(file -> Map.entry(UUID.fromString(file.getUuid()), file.getName()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return Stream.of(notices, documents)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

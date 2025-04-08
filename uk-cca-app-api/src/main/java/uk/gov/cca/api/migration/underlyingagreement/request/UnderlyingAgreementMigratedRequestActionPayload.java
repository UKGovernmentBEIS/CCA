package uk.gov.cca.api.migration.underlyingagreement.request;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmittedRequestActionPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementMigratedRequestActionPayload extends UnderlyingAgreementSubmittedRequestActionPayload {
    
    @NotNull
    private FileInfoDTO underlyingAgreementDocument;
    
    @NotNull
    private LocalDateTime activationDate;

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(
                super.getFileDocuments(),
                Map.of(UUID.fromString(underlyingAgreementDocument.getUuid()), underlyingAgreementDocument.getName())
        ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

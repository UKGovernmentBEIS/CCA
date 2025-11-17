package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementAcceptedRequestActionPayload extends UnderlyingAgreementReviewedRequestActionPayload {

	@NotEmpty
    private Map<SchemeVersion, FileInfoDTO> underlyingAgreementDocuments;

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.concat(
                super.getFileDocuments().entrySet().stream(),
                underlyingAgreementDocuments.values().stream().map(file -> Map.entry(UUID.fromString(file.getUuid()), file.getName()))
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

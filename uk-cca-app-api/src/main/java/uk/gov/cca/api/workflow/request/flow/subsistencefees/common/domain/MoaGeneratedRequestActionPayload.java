package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

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

import java.time.Year;
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
public class MoaGeneratedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    private String paymentRequestId;

    @NotNull
    private Year chargingYear;

    @NotNull
    private String transactionId;

    @NotNull
    private FileInfoDTO moaDocument;

    @NotEmpty
    private List<@NotNull DefaultNoticeRecipient> recipients;

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(
                super.getFileDocuments(),
                Map.of(UUID.fromString(moaDocument.getUuid()), moaDocument.getName())
        ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

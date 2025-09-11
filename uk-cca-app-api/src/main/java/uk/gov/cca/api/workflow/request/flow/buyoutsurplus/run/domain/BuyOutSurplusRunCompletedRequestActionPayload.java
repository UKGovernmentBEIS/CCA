package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunSummary;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusRunCompletedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    private BuyOutSurplusRunSummary runSummary;

    private BuyOutSurplusErrorType errorType;

    @Valid
    private FileInfoDTO csvFile;

    // Add system generated file directly to attachments
    @Override
    public Map<UUID, String> getAttachments() {
        return csvFile != null
                ? Map.of(UUID.fromString(csvFile.getUuid()), csvFile.getName())
                : new HashMap<>();
    }
}

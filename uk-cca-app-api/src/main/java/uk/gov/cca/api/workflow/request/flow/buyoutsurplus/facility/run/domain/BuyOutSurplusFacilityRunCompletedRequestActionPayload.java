package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusFacilityRunCompletedRequestActionPayload extends CcaRequestActionPayload {

	@NotNull
    @Valid
    private BuyOutSurplusFacilityRunSummary runSummary;

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

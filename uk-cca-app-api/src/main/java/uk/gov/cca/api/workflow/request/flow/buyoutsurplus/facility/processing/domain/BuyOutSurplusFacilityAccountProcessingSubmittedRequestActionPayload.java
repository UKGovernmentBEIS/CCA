package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {
	
	@NotBlank
    private String runId;

    private List<@Valid @NotNull BuyOutSurplusFacilityTransactionSummary> summary;

    private List<@Valid @NotNull DefaultNoticeRecipient> defaultContacts;
    
    @NotEmpty
    private List<@Valid @NotNull BuyOutSurplusFacilityPerformanceDetail> facilities;

    @Override
    public Map<UUID, String> getFileDocuments() {
        if (ObjectUtils.isEmpty(summary)) {
            return Collections.emptyMap();
        }

        return summary.stream()
                .map(BuyOutSurplusFacilityTransactionSummary::getOfficialNotice)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        notice -> UUID.fromString(notice.getUuid()),
                        FileInfoDTO::getName));
    }
}

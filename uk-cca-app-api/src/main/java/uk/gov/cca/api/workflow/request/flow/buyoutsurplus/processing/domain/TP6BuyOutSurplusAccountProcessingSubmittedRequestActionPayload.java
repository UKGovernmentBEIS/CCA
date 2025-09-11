package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @Valid
    @NotNull
    private BuyOutSurplusDetails details;

    @Valid
    private List<DefaultNoticeRecipient> defaultContacts;

    @Override
    public Map<UUID, String> getFileDocuments() {
        if(!ObjectUtils.isEmpty(details) && !ObjectUtils.isEmpty(details.getOfficialNotice())) {
            return Map.of(UUID.fromString(details.getOfficialNotice().getUuid()), details.getOfficialNotice().getName());
        }
        return Collections.emptyMap();
    }
}

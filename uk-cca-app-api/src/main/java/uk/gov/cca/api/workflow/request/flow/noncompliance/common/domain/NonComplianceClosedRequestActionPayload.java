package uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonComplianceClosedRequestActionPayload extends CcaRequestActionPayload {

    @NotNull
    @Valid
    @JsonUnwrapped
    private NonComplianceCloseJustification closeJustification;

    @Builder.Default
    private Map<UUID, String> nonComplianceAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getNonComplianceAttachments();
    }
}

package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload extends CcaRequestActionPayload {

    @Valid
    @NotNull
    private FacilityPerformanceAccountTemplateDataUpload details;

    @Valid
    @NotNull
    private FacilityPerformanceAccountTemplateDataUploadResults results;

    @Builder.Default
    @NotEmpty
    private Map<UUID, String> uploadAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getUploadAttachments();
    }
}

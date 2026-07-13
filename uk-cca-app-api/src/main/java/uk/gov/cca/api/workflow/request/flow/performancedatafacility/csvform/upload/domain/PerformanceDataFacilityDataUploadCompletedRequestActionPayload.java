package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDataUploadCompletedRequestActionPayload extends CcaRequestActionPayload {

	@NotNull
	@Valid
	private PerformanceDataFacilityUpload performanceDataUpload;
	
	@NotNull
	@Valid
	private PerformanceDataFacilityUploadResults results;
	
	@Builder.Default
	@NotEmpty
    private Map<UUID, String> uploadAttachments = new HashMap<>();
	
	@Override
    public Map<UUID, String> getAttachments() {
        return this.getUploadAttachments();
    }
}

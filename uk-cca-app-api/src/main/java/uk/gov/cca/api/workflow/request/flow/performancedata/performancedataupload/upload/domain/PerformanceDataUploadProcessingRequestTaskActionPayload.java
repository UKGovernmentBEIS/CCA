package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataUploadProcessingRequestTaskActionPayload extends RequestTaskActionPayload {

    @Valid
    @NotNull
    private PerformanceDataUpload performanceDataUpload;
}

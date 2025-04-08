package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.utils.PerformanceAccountTemplateDataUploadErrorType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload extends RequestTaskPayload {

	@JsonUnwrapped
	private PerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload;
	
	private FileReports fileReports;
	
	private FileInfoDTO csvReportFile;
	
	private PerformanceAccountTemplateDataUploadProcessingStatus processingStatus;

	private PerformanceAccountTemplateDataUploadErrorType errorType;
	
	@Builder.Default
	private Map<UUID, String> uploadAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
		Map<UUID, String> attachments = getUploadAttachments();

		if (csvReportFile != null) {
			attachments.put(UUID.fromString(csvReportFile.getUuid()), csvReportFile.getName());
		}

		return attachments;
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
		return getAttachments().keySet();
    }
    
}

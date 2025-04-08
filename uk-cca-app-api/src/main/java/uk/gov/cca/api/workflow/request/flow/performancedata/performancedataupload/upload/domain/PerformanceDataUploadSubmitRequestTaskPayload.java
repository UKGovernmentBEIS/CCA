package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataUploadSubmitRequestTaskPayload extends RequestTaskPayload {

	private SectorAssociationInfo sectorAssociationInfo;
	private PerformanceDataUpload performanceDataUpload;
	private Boolean processCompleted;
	private String errorMessage;
	private FileInfoDTO csvFile;
	private Integer totalFilesUploaded;
	private Integer filesSucceeded;
	private Integer filesFailed;

	@Builder.Default
	private Map<String, String> errors = new HashMap<>();

	@Builder.Default
	private Map<Long, TargetUnitAccountUploadReport> accountReports = new HashMap<>();

	@Builder.Default
	private Map<UUID, String> performanceDataUploadAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
		Map<UUID, String> attachments = getPerformanceDataUploadAttachments();

		if (csvFile != null) {
			attachments.put(UUID.fromString(csvFile.getUuid()), csvFile.getName());
		}

		return attachments;
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
		return getAttachments().keySet();
    }
}

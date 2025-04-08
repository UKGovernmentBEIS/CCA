package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
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
public class PerformanceDataDownloadSubmitRequestTaskPayload extends RequestTaskPayload {

	private SectorAssociationInfo sectorAssociationInfo;
	private PerformanceDataTargetPeriodType targetPeriodType;
	private FileInfoDTO zipFile;
	private FileInfoDTO errorsFile;
	private Boolean processCompleted;
	private String errorMessage;

    @Override
    public Map<UUID, String> getAttachments() {
		Map<UUID, String> attachments = new HashMap<>();

		if (zipFile != null) {
			attachments.put(UUID.fromString(zipFile.getUuid()), zipFile.getName());
		}

		if (errorsFile != null) {
			attachments.put(UUID.fromString(errorsFile.getUuid()), errorsFile.getName());
		}
        
		return attachments;
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
		return getAttachments().keySet();
    }
	
}

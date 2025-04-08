package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataSpreadsheetProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

    private PerformanceDataTargetPeriodType performanceDataTargetPeriodType;
    private FileInfoDTO accountReportFile;
    private PerformanceData performanceData;

	// Add system generated file directly to attachments
    @Override
	public Map<UUID, String> getAttachments() {
		return accountReportFile != null
				? Map.of(UUID.fromString(accountReportFile.getUuid()), accountReportFile.getName())
				: new HashMap<>();
	}
}

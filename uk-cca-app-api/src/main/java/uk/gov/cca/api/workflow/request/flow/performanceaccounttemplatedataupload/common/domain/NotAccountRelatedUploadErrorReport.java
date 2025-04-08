package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotAccountRelatedUploadErrorReport {

	private String fileName;
	private String error;
	
}

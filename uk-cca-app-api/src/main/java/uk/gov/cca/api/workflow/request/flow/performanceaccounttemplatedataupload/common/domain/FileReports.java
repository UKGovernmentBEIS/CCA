package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileReports {

	@Builder.Default
	private Map<Long, AccountUploadReport> accountFileReports = new HashMap<>();
	
	@Builder.Default
	private List<NotAccountRelatedUploadErrorReport> notAccountRelatedFileErrors = new ArrayList<>();
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public int getNumberOfFilesSucceeded() {
		return accountFileReports.values()
		        .stream()
		        .filter(accountFileReport -> Boolean.TRUE.equals(accountFileReport.getSucceeded()))
				.toList()
				.size();
	}
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public int getNumberOfFilesFailed() {
		return accountFileReports.values()
		        .stream()
		        .filter(accountFileReport -> Boolean.FALSE.equals(accountFileReport.getSucceeded()))
		        .mapToInt(accountFileReport -> accountFileReport.getErrorFilenames().size())
		        .sum() + notAccountRelatedFileErrors.size();
	}
	
}

package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceAccountTemplateDataUploadErrorType {

	CSV_GENERATION_FAILED("Failed to generate CSV file"),
	REPORT_PACKAGE_MISSING("Report package is missing"),
	EXTRACT_VALIDATE_PERSIST_GENERIC_ERROR("Extract validation and persist successfull process failed")
	;
	
	private final String description;
	
}

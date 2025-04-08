package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum FilenameValidationErrorType {


	INVALID_FILE_NAME("File name is invalid"),
	NOT_RELATED_TO_ELIGIBLE_ACCOUNT("Is not related to an eligible target unit account"),
	SECTOR_IS_NOT_USERS_SECTOR("Sector is not the same as the user's sector"),
	TARGET_PERIOD_NOT_MATCH_WITH_SELECTED_ONE("File target period is not the same as the selected target period"),
	MULTIPLE_FILES_PER_ACCOUNT("Multiple files per target unit account exist"),
	INTERNAL_SERVER_ERROR("Internal server error occurred")
	;
	
	private final String description;
	
}

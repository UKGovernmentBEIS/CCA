package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation;

import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.validation.RequestTaskActionFileValidator;

@Component
public class PerformanceDataUploadAttachReportPackageValidator implements RequestTaskActionFileValidator {
	
	@Override
	public void validate(@Valid FileDTO file) {
		if(!FileType.ZIP.getMimeTypes().contains(MimeTypeUtils.detect(file.getFileContent(), file.getFileName()))) {
			throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPLOAD_FILE_TYPE);
		}
	}

	@Override
	public Set<String> getRequestTaskActionTypes() {
		return Set.of(CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE);
	}
}

package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.validation.RequestTaskActionFileValidator;

import java.util.Set;

@Component
public class FacilityPerformanceAccountTemplateDataUploadAttachmentValidator implements RequestTaskActionFileValidator {

    @Override
    public void validate(@Valid FileDTO fileDTO) {
        if (!MimeTypeUtils.detect(fileDTO.getFileContent(), fileDTO.getFileName()).equals("text/csv")) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_FILE_TYPE);
        }
    }

    @Override
    public Set<String> getRequestTaskActionTypes() {
        return Set.of(CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACHMENT);
    }
}
package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateSectorRelatedValidator;

@Service
public class PerformanceAccountTemplateDataUploadCreateValidator extends RequestCreateSectorRelatedValidator {

    public PerformanceAccountTemplateDataUploadCreateValidator(CcaRequestCreateValidatorService ccaRequestCreateValidatorService) {
        super(ccaRequestCreateValidatorService);
    }

    @Override
    protected Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD;
    }
}


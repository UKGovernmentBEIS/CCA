package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.validation.PerformanceDataCreateValidator;

import java.util.Set;

@Service
public class PerformanceDataUploadCreateValidator extends PerformanceDataCreateValidator {

    public PerformanceDataUploadCreateValidator(TargetPeriodService targetPeriodService,
                                                  CcaRequestCreateValidatorService ccaRequestCreateValidatorService) {
        super(targetPeriodService, ccaRequestCreateValidatorService);
    }

    @Override
    protected Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.PERFORMANCE_DATA_UPLOAD);
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_UPLOAD;
    }
}

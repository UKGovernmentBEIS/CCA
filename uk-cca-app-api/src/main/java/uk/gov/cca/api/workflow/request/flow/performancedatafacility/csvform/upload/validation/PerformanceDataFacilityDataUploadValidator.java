package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadValidator {

    private final TargetPeriodService targetPeriodService;
    private final DataValidator<PerformanceDataFacilityUpload> dataValidator;
    private final PerformanceDataFacilityValidator performanceDataFacilityValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public void validate(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload, LocalDate submissionDate) {
        final PerformanceDataFacilityUpload performanceDataUpload = taskPayload.getPerformanceDataUpload();
        List<PerformanceDataFacilityViolation> violations = new ArrayList<>();

        if(!taskPayload.getProcessingStatus().equals(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_PROCESS_STATUS);
        }

        if (ObjectUtils.isEmpty(performanceDataUpload)) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityUpload.class.getName(),
                    PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_PERFORMANCE_DATA));
        } else {
            // Validate data
            dataValidator.validate(performanceDataUpload)
                    .map(businessViolation ->
                            new PerformanceDataFacilityViolation(PerformanceDataFacilityUpload.class.getName(),
                                    PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_PERFORMANCE_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);

            // Validate report submission
            final TargetPeriodDetailsDTO targetPeriod = targetPeriodService
                    .getTargetPeriodDetailsByTargetPeriodType(performanceDataUpload.getTargetPeriodType());
            List<PerformanceDataFacilityViolation> reportValidations = performanceDataFacilityValidator
                    .validateReportSubmission(targetPeriod, performanceDataUpload.getReportType(), submissionDate)
                    .getViolations().stream()
                    .map(PerformanceDataFacilityViolation.class::cast)
                    .toList();
            violations.addAll(reportValidations);

            // Validate files
            validateFiles(taskPayload, violations);
        }

        if (!violations.isEmpty()) {
            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_DATA, violations);
        }
    }

    private void validateFiles(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload,
                               List<PerformanceDataFacilityViolation> violations) {
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getUploadAttachments().keySet())) {
            violations.add(new PerformanceDataFacilityViolation(PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.ATTACHMENT_NOT_FOUND));
        }
    }
}

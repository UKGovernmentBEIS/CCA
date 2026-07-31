package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.config.PerformanceAccountTemplateConfig;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadValidator {

    private final TargetPeriodService targetPeriodService;
    private final PerformanceAccountTemplateConfig performanceAccountTemplateConfig;
    private final DataValidator<FacilityPerformanceAccountTemplateDataUpload> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public boolean isAvailable() {
        final LocalDate today = LocalDate.now();

        final Year targetYear = Optional.ofNullable(performanceAccountTemplateConfig.getTargetYear())
                .orElse(Year.now().minusYears(1));

        final LocalDate submissionDate = Optional.ofNullable(performanceAccountTemplateConfig.getSubmissionDate())
                .orElse(today);

        return isSubmissionDateValidForReportingPeriod(targetYear, submissionDate)
                && isInActiveTargetPeriod(targetYear);
    }

    public void validate(FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload, LocalDate submissionDate) {
        final FacilityPerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = taskPayload.getPerformanceAccountTemplateDataUpload();
        List<FacilityPerformanceAccountTemplateDataViolation> violations = new ArrayList<>();

        if (!taskPayload.getProcessingStatus().equals(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET)) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_PROCESS_STATUS);
        }

        if (ObjectUtils.isEmpty(performanceAccountTemplateDataUpload)) {
            violations.add(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUpload.class.getName(),
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA));
        } else {
            // Validate data
            dataValidator.validate(performanceAccountTemplateDataUpload)
                    .map(businessViolation ->
                            new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUpload.class.getName(),
                                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);

            // Validate report submission date
            if (violations.isEmpty()) {
                boolean submissionDateValidForReportingPeriod =
                        isSubmissionDateValidForReportingPeriod(performanceAccountTemplateDataUpload.getTargetYear(), submissionDate);
                if (!submissionDateValidForReportingPeriod) {
                    violations.add(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUpload.class.getName(),
                            FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.PERFORMANCE_ACCOUNT_TEMPLATE_SUBMISSION_DATE_HAS_EXPIRED));
                }
            }

            // Validate files
            validateFiles(taskPayload, violations);
        }

        if (!violations.isEmpty()) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_DATA, violations);
        }
    }

    private void validateFiles(FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload,
                               List<FacilityPerformanceAccountTemplateDataViolation> violations) {
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getUploadAttachments().keySet())) {
            violations.add(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.ATTACHMENT_NOT_FOUND));
        }
    }

    private boolean isSubmissionDateValidForReportingPeriod(Year targetYear, LocalDate submissionDate) {
        LocalDate reportingPeriodStartDate = LocalDate.of(targetYear.plusYears(1).getValue(), 1, 1);
        LocalDate reportingPeriodEndDate = LocalDate.of(targetYear.plusYears(1).getValue(), 12, 31);

        return !submissionDate.isBefore(LocalDate.now()) &&
                !submissionDate.isAfter(reportingPeriodEndDate) && !submissionDate.isBefore(reportingPeriodStartDate);
    }

    private boolean isInActiveTargetPeriod(Year targetYear) {
        List<TargetPeriodDetailsDTO> activeTargetPeriods = targetPeriodService.getTargetPeriodDetailsBySchemeVersionAndStartDateDesc(SchemeVersion.CCA_3);

        return activeTargetPeriods.stream()
                .flatMap(tp -> tp.getTargetPeriodYearsContainer().getTargetPeriodYears().stream())
                .map(TargetPeriodYear::getTargetYear)
                .anyMatch(targetYear::equals);
    }
}

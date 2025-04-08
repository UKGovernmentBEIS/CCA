package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TP6PerformanceDataContextValidator implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = validatePerformanceData(referenceDetails, performanceData);
        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    private List<PerformanceDataUploadViolation> validatePerformanceData(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();

        final UnderlyingAgreement underlyingAgreement = referenceDetails.getUnderlyingAgreement()
                .getUnderlyingAgreementContainer().getUnderlyingAgreement();

        // sector
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.SECTOR)
                        .process(referenceDetails.getSectorAcronym(), performanceData.getSector())
        ));

        // targetPeriod
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD)
                        .process(PerformanceDataTargetPeriodType.TP6, performanceData.getTargetPeriod())
        ));

        // reportVersion
        validateReportVersion(referenceDetails, performanceData, violations);

        // targetType
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.TARGET_TYPE)
                        .process(underlyingAgreement.getTargetPeriod6Details().getTargetComposition().getAgreementCompositionType(), performanceData.getTargetType())
        ));

        // templateVersion
        BigDecimal templateVersion = ObjectUtils.isEmpty(performanceData.getTemplateVersion())
                ? null : new BigDecimal(performanceData.getTemplateVersion());
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.TEMPLATE_VERSION)
                        .process(new BigDecimal(referenceDetails.getTargetPeriodDetails().getPerformanceDataTemplateVersion()), templateVersion)
        ));

        return violations;
    }

    private void validateReportVersion(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData,
                                       List<PerformanceDataUploadViolation> violations) {
        // Validate prepopulated data
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateEquals(TP6ParseExcelCellsReferenceEnum.REPORT_VERSION, PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_REPORT_VERSION.getMessage())
                        .process(referenceDetails.getReportVersion(), performanceData.getReportVersion())
        ));

        // Validate filename
        Integer fileReportVersion = PerformanceDataUploadUtility.extractReportVersionFromReportFilename(referenceDetails.getFileName(), PerformanceDataTargetPeriodType.TP6);
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations("",
                TP6PerformanceDataUploadValidationHelper
                        .validateEquals(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.FILE_NAME_REPORT_VERSION_NOT_VALID.getMessage())
                        .process(fileReportVersion, performanceData.getReportVersion())
        ));

        // Validation rule is thrown when: current_date>=buy_out_data_start_date and current_date<secondary_reporting_start_date and version number>1
        final LocalDate buyOutDataStartDate = referenceDetails.getTargetPeriodDetails().getBuyOutStartDate();
        final LocalDate secondaryReportingStartDate = referenceDetails.getTargetPeriodDetails().getSecondaryReportingStartDate();
        if((referenceDetails.getUploadedDate().isAfter(buyOutDataStartDate) || referenceDetails.getUploadedDate().isEqual(buyOutDataStartDate))
                && referenceDetails.getUploadedDate().isBefore(secondaryReportingStartDate)
                && performanceData.getReportVersion() > 1) {
            violations.add(new PerformanceDataUploadViolation(
                    "",
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA,
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.REPORT_VERSION_NOT_VALID_FOR_PRIMARY_REPORTING.getMessage()));
        }
    }
}

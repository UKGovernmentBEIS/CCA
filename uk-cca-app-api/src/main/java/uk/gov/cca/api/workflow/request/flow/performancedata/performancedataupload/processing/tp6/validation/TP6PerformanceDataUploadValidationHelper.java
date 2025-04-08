package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import lombok.experimental.UtilityClass;

import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.common.validation.BiFunctionalValidation;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BiFunctionalValidator;
import uk.gov.cca.api.common.validation.FunctionalValidation;
import uk.gov.cca.api.common.validation.FunctionalValidator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility.DIGIT_VALIDATION_TOLERANCE;

@UtilityClass
public class TP6PerformanceDataUploadValidationHelper {

    public FunctionalValidator<Object> validateNotEmpty(TP6ParseExcelCellsReferenceEnum excelEnum) {
        String errorMessage = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " +
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_EMPTY_DATA.getMessage();

        return FunctionalValidation.from(value -> !ObjectUtils.isEmpty(value), errorMessage);
    }

    public FunctionalValidator<Object> validateIsEmpty(TP6ParseExcelCellsReferenceEnum excelEnum) {
        String errorMessage = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " +
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_NOT_EMPTY_DATA.getMessage();

        return FunctionalValidation.from(ObjectUtils::isEmpty, errorMessage);
    }

    public FunctionalValidator<Object> validateValueIsZero(TP6ParseExcelCellsReferenceEnum excelEnum) {
        String errorMessage = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " +
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_NOT_EMPTY_DATA.getMessage();

        return FunctionalValidation.from(param -> ((BigDecimal)param).compareTo(BigDecimal.ZERO) == 0, errorMessage);
    }

    public BiFunctionalValidator<Object> validateEquals(String errorMessage) {
        return BiFunctionalValidation.from(ObjectUtils::nullSafeEquals, errorMessage);
    }

    public BiFunctionalValidator<Object> validateEquals(TP6ParseExcelCellsReferenceEnum excelEnum, String errorMessage) {
        String message = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " + errorMessage;

        return BiFunctionalValidation.from(ObjectUtils::nullSafeEquals, message);
    }

    public BiFunctionalValidator<Object> validateReferenceEquals(TP6ParseExcelCellsReferenceEnum excelEnum) {
        return validateEquals(excelEnum, PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_PREPOPULATED_DATA.getMessage());
    }

    public BiFunctionalValidator<BigDecimal> validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum excelEnum) {
        String errorMessage = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " +
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_PREPOPULATED_DATA.getMessage();

        return BiFunctionalValidation.from((expected, actual) ->
                        ObjectUtils.isEmpty(expected)
                                ? ObjectUtils.isEmpty(actual)
                                : !ObjectUtils.isEmpty(actual) && expected.compareTo(actual) == 0,
                errorMessage);
    }

    public BiFunctionalValidator<Object> validateCalculateEquals(TP6ParseExcelCellsReferenceEnum excelEnum) {
        return validateEquals(excelEnum, PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_CALCULATED_DATA.getMessage());
    }

    public BiFunctionalValidator<BigDecimal> validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum excelEnum) {
        String errorMessage = PerformanceDataUploadUtility.getExcelCell(excelEnum.getReferenceEnum().getRowIndex(), excelEnum.getReferenceEnum().getColumnIndex()) +
                " " +
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_CALCULATED_DATA.getMessage();

        // Compare with tolerance
        return BiFunctionalValidation.from((expected, actual) -> {
            if (ObjectUtils.isEmpty(expected) && ObjectUtils.isEmpty(actual)) {
                return true;
            } else if ((ObjectUtils.isEmpty(expected) && !ObjectUtils.isEmpty(actual))
                    || (!ObjectUtils.isEmpty(expected) && ObjectUtils.isEmpty(actual))) {
                return false;
            } else if (expected.compareTo(BigDecimal.ZERO) == 0 || actual.compareTo(BigDecimal.ZERO) == 0) {
                return actual.subtract(expected).abs().compareTo(DIGIT_VALIDATION_TOLERANCE) < 0;
            } else {
                BigDecimal div = actual.divide(expected, MathContext.DECIMAL128);
                BigDecimal result = BigDecimal.ONE.subtract(div).abs();

                return result.compareTo(BigDecimal.ZERO) == 0 || result.compareTo(DIGIT_VALIDATION_TOLERANCE) < 0;
            }
        }, errorMessage);
    }

    public List<PerformanceDataUploadViolation> getViolations(String section, BusinessValidationResult validationResult) {
        if (validationResult.isValid()) {
            return List.of();
        }

        return validationResult.getViolations().stream()
                .map(violation -> new PerformanceDataUploadViolation(
                        section,
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA,
                        violation.getData()))
                .toList();
    }
}

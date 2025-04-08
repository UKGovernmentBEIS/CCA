package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.validation.PerformanceDataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.validation.PerformanceDataSpreadsheetTemplateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TP6PerformanceDataSpreadsheetTemplateValidator extends PerformanceDataValidator<TP6PerformanceData> implements PerformanceDataSpreadsheetTemplateValidator<TP6PerformanceData> {

    private final List<TP6PerformanceDataSectionContextValidator> tP6PerformanceDataSectionContextValidators;

    public TP6PerformanceDataSpreadsheetTemplateValidator(DataValidator<TP6PerformanceData> validator, List<TP6PerformanceDataSectionContextValidator> tP6PerformanceDataSectionContextValidators) {
        super(validator);
        this.tP6PerformanceDataSectionContextValidators = tP6PerformanceDataSectionContextValidators;
    }

    @Override
    public BusinessValidationResult validateData(TP6PerformanceData performanceData) {
        return super.validate(performanceData);
    }

    @Override
    public List<BusinessValidationResult> validateBusinessData(PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Perform business validations
        tP6PerformanceDataSectionContextValidators.forEach(v ->
                validationResults.add(v.validate(referenceDetails, performanceData)));

        return validationResults;
    }

    @Override
    protected List<String> constructViolationData(Set<ConstraintViolation<TP6PerformanceData>> constraintViolations) {
        List<String> violationData = new ArrayList<>();

        constraintViolations.forEach(constraintViolation -> {
            String field = Optional.ofNullable(constraintViolation.getPropertyPath())
                    .map(Path::toString)
                    .map(name -> name.replace(".<map value>", "").replace(".<map key>", ""))
                    .orElse("");

            violationData.add(String.format("%s %s",
                    TP6ParseExcelCellsReferenceEnum.getExcelRowColumn(field),
                    constraintViolation.getMessage()));
        });

        return violationData;
    }

    @Override
    public TargetPeriodDocumentTemplate getDocumentTemplateType() {
        return TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;
    }
}

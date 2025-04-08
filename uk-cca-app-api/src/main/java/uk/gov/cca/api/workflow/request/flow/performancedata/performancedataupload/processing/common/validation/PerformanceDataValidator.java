package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.validation;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public abstract class PerformanceDataValidator<T extends PerformanceData> {

    private final DataValidator<T> validator;

    public BusinessValidationResult validate(T performanceData) {
        // Invoke data validation
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();
        validator.validate(performanceData, this::constructViolationData)
                .map(businessViolation -> new PerformanceDataUploadViolation(
                        businessViolation.getSectionName(),
                        PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA,
                        businessViolation.getData()))
                .ifPresent(violations::add);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    protected abstract List<String> constructViolationData(Set<ConstraintViolation<T>> constraintViolations);
}

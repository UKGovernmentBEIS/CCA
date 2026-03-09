package uk.gov.cca.api.workflow.request.flow.noncompliance.details.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NonComplianceDetailsSubmitValidator {

    private final DataValidator<NonComplianceDetails> dataValidator;

    public void validate(final NonComplianceDetailsSubmitRequestTaskPayload taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getNonComplianceDetails())) {
            violations.add(new NonComplianceViolation(NonComplianceDetails.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_DETAILS_DATA));
        } else {
            dataValidator.validate(taskPayload.getNonComplianceDetails())
                    .map(businessViolation ->
                            new NonComplianceViolation(NonComplianceDetails.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_DETAILS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        boolean isValid = violations.isEmpty();

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_NON_COMPLIANCE, violations);
        }
    }

}

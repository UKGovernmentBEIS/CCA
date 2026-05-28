package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonCompliancePenaltyOutcomeType;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NonComplianceConclusionCompleteValidator {

    private final NonComplianceConclusionSubmitValidator nonComplianceConclusionSubmitValidator;

    public void validate(final NonComplianceConclusionSubmitRequestTaskPayload taskPayload) {
        final List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate submit data
        validationResults.add(nonComplianceConclusionSubmitValidator.validate(taskPayload));

        // Validate consistency data
        if (taskPayload.getNonComplianceConclusion().getDetails().getPenaltyOutcome().equals(NonCompliancePenaltyOutcomeType.WITHDRAW)) {
            validationResults.add(BusinessValidationResult.builder()
                    .valid(false)
                    .violations(List.of(new NonComplianceViolation(NonComplianceConclusion.class.getName(),
                            NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_CONCLUSION_DATA)))
                    .build());
        }

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_NON_COMPLIANCE, ValidatorHelper.extractViolations(validationResults));
        }
    }
}

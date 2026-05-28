package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.validation;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class NonComplianceAppealOutcomeSubmitValidator {

    private final DataValidator<NonComplianceAppealOutcomeDetails> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public void validate(final NonComplianceAppealOutcomeSubmitRequestTaskPayload taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getAppealOutcome())) {
            violations.add(new NonComplianceViolation(NonComplianceAppealOutcomeDetails.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_OUTCOME_DETAILS));
        } else {
            dataValidator.validate(taskPayload.getAppealOutcome())
                    .map(businessViolation ->
                            new NonComplianceViolation(NonComplianceAppealOutcomeDetails.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_OUTCOME_DETAILS,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getNonComplianceAttachments().keySet())) {
            violations.add(new NonComplianceViolation(NonComplianceViolation.NonComplianceViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        boolean isValid = violations.isEmpty();

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_NON_COMPLIANCE, violations);
        }
    }
}

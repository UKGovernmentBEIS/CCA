package uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAppealable;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NonComplianceAppealDetailsSubmitValidator {

    private final DataValidator<NonComplianceAppealDetails> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public void validate(final NonComplianceRequestTaskAppealable taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getAppealDetails())) {
            violations.add(new NonComplianceViolation(NonComplianceAppealDetails.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_DETAILS));
        } else {
            dataValidator.validate(taskPayload.getAppealDetails())
                    .map(businessViolation ->
                            new NonComplianceViolation(NonComplianceAppealDetails.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_APPEAL_DETAILS,
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

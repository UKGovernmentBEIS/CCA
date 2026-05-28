package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusion;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NonComplianceConclusionSubmitValidator {

    private final DataValidator<NonComplianceConclusion> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final NonComplianceConclusionSubmitRequestTaskPayload taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getNonComplianceConclusion())) {
            violations.add(new NonComplianceViolation(NonComplianceConclusion.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_CONCLUSION_DATA));
        } else {
            dataValidator.validate(taskPayload.getNonComplianceConclusion())
                    .map(businessViolation ->
                            new NonComplianceViolation(NonComplianceConclusion.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_CONCLUSION_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getNonComplianceAttachments().keySet())) {
            violations.add(new NonComplianceViolation(NonComplianceViolation.NonComplianceViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
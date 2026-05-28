package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EnforcementResponseNoticeSubmitValidator {

    private final DataValidator<NonComplianceEnforcementResponseNotice> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getEnforcementResponseNotice())) {
            violations.add(new NonComplianceViolation(NonComplianceEnforcementResponseNotice.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_DATA));
        } else {
            dataValidator.validate(taskPayload.getEnforcementResponseNotice())
                    .map(businessViolation ->
                            new NonComplianceViolation(NonComplianceEnforcementResponseNotice.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);

            if (taskPayload.isPenaltyReissue()
                    && taskPayload.getEnforcementResponseNotice().getType() == NonComplianceEnforcementResponseNoticeType.PENALTY_WAIVER) {
                violations.add(new NonComplianceViolation(NonComplianceEnforcementResponseNotice.class.getName(),
                        NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_DATA));
            }
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
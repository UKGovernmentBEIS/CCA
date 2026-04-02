package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation.NonComplianceViolation;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class NoticeOfIntentSubmitValidator {

    private final DataValidator<NoticeOfIntent> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final NonComplianceNoticeOfIntentSubmitRequestTaskPayload taskPayload) {
        List<NonComplianceViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getNoticeOfIntent())) {
            violations.add(new NonComplianceViolation(NoticeOfIntent.class.getName(),
                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_NOTICE_OF_INTENT_DATA));
        } else {
            dataValidator.validate(taskPayload.getNoticeOfIntent())
                    .map(businessViolation ->
                            new NonComplianceViolation(NoticeOfIntent.class.getName(),
                                    NonComplianceViolation.NonComplianceViolationMessage.INVALID_NON_COMPLIANCE_NOTICE_OF_INTENT_DATA,
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

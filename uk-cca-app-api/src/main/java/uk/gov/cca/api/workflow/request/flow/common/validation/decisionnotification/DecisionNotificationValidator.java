package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecisionNotificationValidator {

    private final DataValidator<CcaDecisionNotification> ccaDecisionNotificationDataValidator;
    private final CcaDecisionNotificationUsersValidator ccaDecisionNotificationUsersValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validateDecisionNotification(final RequestTask requestTask,
                                                                 final CcaDecisionNotification decisionNotification,
                                                                 final AppUser appUser) {
        List<DecisionNotificationViolation> violations = new ArrayList<>();

        // Validate data
        ccaDecisionNotificationDataValidator.validate(decisionNotification)
                .map(businessViolation ->
                        new DecisionNotificationViolation(CcaDecisionNotification.class.getName(),
                                DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_DECISION_NOTIFICATION_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);

        // Validate users
        List<DecisionNotificationViolation> decisionNotificationViolations = ccaDecisionNotificationUsersValidator
                .validate(requestTask, decisionNotification, appUser).stream()
                .map(businessViolation ->
                        new DecisionNotificationViolation(CcaDecisionNotificationUsersValidator.class.getName(),
                                DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_NOTIFICATION_USERS,
                                businessViolation.getData()))
                .toList();
        violations.addAll(decisionNotificationViolations);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    public BusinessValidationResult validateUnderlyingAgreementFiles(final Set<UUID> referencedAttachmentIds, final Map<UUID,String> attachments) {
        List<DecisionNotificationViolation> violations = new ArrayList<>();

        // Validate files
        if(!fileAttachmentsExistenceValidator
                .valid(referencedAttachmentIds, attachments.keySet())) {
            violations.add(new DecisionNotificationViolation(FileAttachmentsExistenceValidator.class.getName(),
                    DecisionNotificationViolation.DecisionNotificationViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}

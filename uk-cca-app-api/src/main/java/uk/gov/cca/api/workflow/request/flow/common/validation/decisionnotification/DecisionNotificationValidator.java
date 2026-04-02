package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionNotificationValidator {

    private final DataValidator<DecisionNotification> decisionNotificationDataValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;


    public BusinessValidationResult validate(final RequestTask requestTask,
                                             final DecisionNotification decisionNotification,
                                             final AppUser appUser) {
        List<DecisionNotificationViolation> violations = new ArrayList<>();

        // Validate data
        decisionNotificationDataValidator.validate(decisionNotification)
                .map(businessViolation ->
                        new DecisionNotificationViolation(DecisionNotification.class.getName(),
                                DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_DECISION_NOTIFICATION_DATA,
                                businessViolation.getData()))
                .ifPresent(violations::add);

        // Validate users
        if (!decisionNotificationUsersValidator
                .areUsersValid(requestTask, decisionNotification, appUser)) {
            violations.add(new DecisionNotificationViolation(DecisionNotificationUsersValidator.class.getName(),
                    DecisionNotificationViolation.DecisionNotificationViolationMessage.INVALID_NOTIFICATION_USERS));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}

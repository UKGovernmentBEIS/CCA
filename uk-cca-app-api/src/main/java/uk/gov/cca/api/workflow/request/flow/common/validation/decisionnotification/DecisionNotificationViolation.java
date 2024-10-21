package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DecisionNotificationViolation extends BusinessViolation {

    private String message;

    public DecisionNotificationViolation(DecisionNotificationViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public DecisionNotificationViolation(String sectionName, DecisionNotificationViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum DecisionNotificationViolationMessage {
        INVALID_DECISION_NOTIFICATION_DATA ("Invalid decision notification data"),
        INVALID_NOTIFICATION_USERS ("Invalid notification users"),
        ATTACHMENT_NOT_FOUND("Attachment not found")
        ;

        private final String message;

        DecisionNotificationViolationMessage(String message) {
            this.message = message;
        }
    }
}

package uk.gov.cca.api.workflow.request.flow.admintermination.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminTerminationViolation extends BusinessViolation {

    private String message;

    public AdminTerminationViolation(AdminTerminationViolation.AdminTerminationViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public AdminTerminationViolation(String sectionName, AdminTerminationViolation.AdminTerminationViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum AdminTerminationViolationMessage {
        INVALID_ADMIN_TERMINATION_REASON_DATA ("Invalid admin termination reason data"),
        INVALID_DECISION_NOTIFICATION_DATA ("Invalid decision notification data"),
        INVALID_NOTIFICATION_USERS ("Invalid notification users"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_ADMIN_TERMINATION_FINAL_DECISION_REASON_DATA ("Invalid admin termination final decision reason data"),
        INVALID_ADMIN_TERMINATION_WITHDRAW_REASON_DATA ("Invalid admin termination withdraw reason data");

        private final String message;

        AdminTerminationViolationMessage(String message) {
            this.message = message;
        }
    }
}

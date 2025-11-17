package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FacilityAuditViolation extends BusinessViolation {

    private String message;

    public FacilityAuditViolation(FacilityAuditViolation.FacilityAuditViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public FacilityAuditViolation(String sectionName, FacilityAuditViolation.FacilityAuditViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum FacilityAuditViolationMessage {
        INVALID_PRE_AUDIT_MATERIAL_REVIEW_DATA("Invalid pre audit material review data"),
        INVALID_AUDIT_DETAILS_CORRECTIVE_ACTIONS_DATA("Invalid audit details and corrective actions data"),
        INVALID_TRACK_CORRECTIVE_ACTIONS_DATA("Invalid audit track corrective actions data"),
        MISSING_CORRECTIVE_ACTION_RESPONSES("Not all corrective actions have been responded"),
        REFERENCE_NOT_FOUND("Reference not found"),
        ATTACHMENT_NOT_FOUND("Attachment not found");

        private final String message;

        FacilityAuditViolationMessage(String message) {
            this.message = message;
        }
    }
}

package uk.gov.cca.api.workflow.request.flow.noncompliance.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class NonComplianceViolation extends BusinessViolation {

    private String message;

    public NonComplianceViolation(NonComplianceViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public NonComplianceViolation(String sectionName, NonComplianceViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum NonComplianceViolationMessage {
        INVALID_NON_COMPLIANCE_DETAILS_DATA("Invalid Non-compliance details data");

        private final String message;

        NonComplianceViolationMessage(String message) {
            this.message = message;
        }
    }
}

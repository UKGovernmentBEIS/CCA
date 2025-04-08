package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubsistenceFeesViolation extends BusinessViolation {

    private String message;

    public SubsistenceFeesViolation(SubsistenceFeesViolation.SubsistenceFeesViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public SubsistenceFeesViolation(String sectionName, SubsistenceFeesViolation.SubsistenceFeesViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }


    @Getter
    public enum SubsistenceFeesViolationMessage {
        GENERATE_SECTOR_MOA_FAILED("Generate Sector MOA failed"),
        GENERATE_TARGET_UNIT_MOA_FAILED("Generate Target Unit MOA failed");

        private final String message;

        SubsistenceFeesViolationMessage(String message) {
            this.message = message;
        }
    }
}

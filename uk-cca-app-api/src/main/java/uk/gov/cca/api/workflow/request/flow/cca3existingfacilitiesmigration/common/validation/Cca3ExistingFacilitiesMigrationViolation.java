package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cca3ExistingFacilitiesMigrationViolation extends BusinessViolation {

    private String message;

    public Cca3ExistingFacilitiesMigrationViolation(Cca3ExistingFacilitiesMigrationViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public Cca3ExistingFacilitiesMigrationViolation(String sectionName, Cca3ExistingFacilitiesMigrationViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum Cca3ExistingFacilitiesMigrationViolationMessage {
        ACCOUNT_NOT_ELIGIBLE_FOR_MIGRATION("Target unit account not eligible for migration"),
        ACCOUNT_FACILITIES_NOT_VALID("Target unit account facilities not valid"),
        ACCOUNT_FACILITY_NAME_NOT_VALID("Target unit account facility name not valid"),
        ACCOUNT_FACILITY_MEASUREMENT_TYPE_NOT_VALID("Target unit account facility measurement type not valid"),

        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_ACTIVATION_DETAILS_DATA("Invalid activation details data"),
        ;

        private final String message;

        Cca3ExistingFacilitiesMigrationViolationMessage(String message) {
            this.message = message;
        }
    }
}

package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmissionsTradingSchemeDataViolation extends BusinessViolation {

    private String message;

    public EmissionsTradingSchemeDataViolation(EmissionsTradingSchemeDataViolationMessage violationMessage) {
        super("", violationMessage.getMessage());
        this.message = violationMessage.getMessage();
    }

    public EmissionsTradingSchemeDataViolation(String sectionName, Object... data) {
        super(sectionName, data);
    }

    public EmissionsTradingSchemeDataViolation(String sectionName, EmissionsTradingSchemeDataViolation.EmissionsTradingSchemeDataViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum EmissionsTradingSchemeDataViolationMessage {
        INVALID_EMISSIONS_TRADING_SCHEME_DATA("Invalid emissions trading scheme data"),
        INVALID_PRIMARY_ENERGY_DATA("Invalid primary energy data"),
        INVALID_CALCULATED_RESULT_DATA("Invalid expected calculated data");

        private final String message;

        EmissionsTradingSchemeDataViolationMessage(String message) {
            this.message = message;
        }
    }
}

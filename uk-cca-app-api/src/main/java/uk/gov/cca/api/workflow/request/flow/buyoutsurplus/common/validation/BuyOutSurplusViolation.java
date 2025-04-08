package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BuyOutSurplusViolation extends BusinessViolation {

    private String message;

    public BuyOutSurplusViolation(BuyOutSurplusViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum BuyOutSurplusViolationMessage {
        PROCESS_FAILED("Target unit account process failed"),
        GENERATE_CSV_FAILED("Generate csv failed"),
        ;

        private final String message;

        BuyOutSurplusViolationMessage(String message) {
            this.message = message;
        }
    }
}

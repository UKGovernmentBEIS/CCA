package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.common.domain;

import lombok.Getter;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.common.validation.BuyOutSurplusViolation;

import java.io.Serial;

@Getter
public class BuyOutSurplusAccountProcessingException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    private final BuyOutSurplusViolation.BuyOutSurplusViolationMessage error;

    public BuyOutSurplusAccountProcessingException(BuyOutSurplusViolation.BuyOutSurplusViolationMessage error) {
        super(error.getMessage());
        this.error = error;
    }
}

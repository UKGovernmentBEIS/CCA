package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import java.io.Serial;

public class BuyOutSurplusFacilityProcessingException extends Exception {

	@Serial
    private static final long serialVersionUID = 1L;
	private final String errorMessage;

    public BuyOutSurplusFacilityProcessingException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

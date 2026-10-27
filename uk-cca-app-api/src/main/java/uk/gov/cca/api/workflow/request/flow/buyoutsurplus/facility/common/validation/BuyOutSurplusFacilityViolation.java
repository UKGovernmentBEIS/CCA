package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@EqualsAndHashCode(callSuper = true)
@Data
public class BuyOutSurplusFacilityViolation extends BusinessViolation {

	private String message;

    public BuyOutSurplusFacilityViolation(BuyOutSurplusFacilityViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public BuyOutSurplusFacilityViolation(
    		BuyOutSurplusFacilityViolationMessage violationMessage,
            Set<TargetPeriodType> targetPeriods) {
        super("", List.of());
        this.message = violationMessage.getMessage(targetPeriods);
    }

    @Getter
    public enum BuyOutSurplusFacilityViolationMessage {
        MISSING_TPRS("Unlocked reports for %s were not submitted before the batch was initiated"),
        AMOUNT_REFUNDED_GREATER_THAN_AMOUNT_RECEIVED("The amount marked as to be refunded for %s is greater than the amount of buy-out fee marked as received"),
        PROCESSING_ERROR("Error while processing the facility"),
        INVALID_DATA("Invalid facility data found while processing the facility"),
        DATA_PERSIST_ERROR("Error while persisting the facility processing results");

        private final String message;

        BuyOutSurplusFacilityViolationMessage(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }

        public String getMessage(Set<TargetPeriodType> targetPeriods) {
        	return String.format(
        	        message,
        	        targetPeriods.stream()
        	        .sorted()
        	        .map(TargetPeriodType::name)
        	        .collect(Collectors.joining(","))
        	    );
        }

        public String getMessage(TargetPeriodType targetPeriod) {
            return String.format(message, targetPeriod.name());
        }
    }
}

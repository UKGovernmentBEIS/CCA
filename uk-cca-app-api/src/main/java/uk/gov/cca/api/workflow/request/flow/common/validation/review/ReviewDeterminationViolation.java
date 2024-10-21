package uk.gov.cca.api.workflow.request.flow.common.validation.review;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewDeterminationViolation extends BusinessViolation {

    private String message;

    public ReviewDeterminationViolation(ReviewDeterminationViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public ReviewDeterminationViolation(String sectionName, ReviewDeterminationViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum ReviewDeterminationViolationMessage {
        INVALID_DETERMINATION_DATA("Invalid determination data"),
        INVALID_REVIEW_DECISION_DATA("Invalid review decision data"),
        INVALID_FACILITY_REVIEW_DECISION_DATA("Invalid facility review decision data");

        private final String message;

        ReviewDeterminationViolationMessage(String message) {
            this.message = message;
        }
    }
}

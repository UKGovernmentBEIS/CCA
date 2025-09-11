package uk.gov.cca.api.workflow.request.flow.common.validation.peerreview;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CcaPeerReviewViolation extends BusinessViolation {

    private String message;

    public CcaPeerReviewViolation(CcaPeerReviewViolation.CcaPeerReviewViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public CcaPeerReviewViolation(String sectionName, CcaPeerReviewViolation.CcaPeerReviewViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum CcaPeerReviewViolationMessage {
        INVALID_PEER_REVIEWER_ASSIGNMENT("Invalid peer reviewer assignment"),
        ATTACHMENT_NOT_FOUND("Attachment not found");

        private final String message;

        CcaPeerReviewViolationMessage(String message) {
            this.message = message;
        }
    }
}

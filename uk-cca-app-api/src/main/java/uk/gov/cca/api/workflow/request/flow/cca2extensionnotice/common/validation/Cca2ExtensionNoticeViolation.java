package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Cca2ExtensionNoticeViolation extends BusinessViolation {

    private String message;

    public Cca2ExtensionNoticeViolation(Cca2ExtensionNoticeViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum Cca2ExtensionNoticeViolationMessage {
        PROCESS_FAILED("Target unit account process failed"),
        ACCOUNT_NOT_ELIGIBLE("Target unit account not eligible for cca2 extension notice"),
        ;

        private final String message;

        Cca2ExtensionNoticeViolationMessage(String message) {
            this.message = message;
        }
    }
}

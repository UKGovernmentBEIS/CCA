package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnderlyingAgreementVariationViolation extends BusinessViolation {

    private String message;

    public UnderlyingAgreementVariationViolation(UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public UnderlyingAgreementVariationViolation(String sectionName, UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum UnderlyingAgreementVariationViolationMessage {
        INVALID_SECTION_DATA("Invalid section data"),
        INVALID_UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_DETAILS_DATA("Invalid underlying agreement variation activation details data"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_APPLICATION_REASON_SECTION("Invalid application reason");

        private final String message;

        UnderlyingAgreementVariationViolationMessage(String message) {
            this.message = message;
        }
    }
}

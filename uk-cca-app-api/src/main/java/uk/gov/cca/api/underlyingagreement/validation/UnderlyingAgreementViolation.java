package uk.gov.cca.api.underlyingagreement.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnderlyingAgreementViolation extends BusinessViolation {

    private String message;

    public UnderlyingAgreementViolation(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    public UnderlyingAgreementViolation(String sectionName, UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage violationMessage, Object... data) {
        super(sectionName, data);
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum UnderlyingAgreementViolationMessage {
        INVALID_SECTION_DATA("Invalid section data"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_ATTACHMENT_TYPE("Invalid attachment type"),
        INVALID_EVIDENCE_ATTACHMENT_TYPE("Invalid evidence attachment type"),
        INVALID_TARGET_CALCULATOR_ATTACHMENT_TYPE("Invalid target calculator attachment type"),

        INVALID_TARGET_UNIT_TYPE("Target unit type not in compliance with sector or subsector scheme"),
        INVALID_TARGET_UNIT_THROUGHPUT_MEASURED("Is target unit throughput measured not in compliance with sector or subsector scheme"),
        INVALID_THROUGHPUT_UNIT("Invalid throughput unit"),
        INVALID_AGREEMENT_COMPOSITION_TYPE("Agreement composition type not valid with data"),
        INVALID_TARGET_COMPOSITION_PERFORMANCE("Invalid target composition performance"),
        INVALID_TARGETS("Invalid targets value"),

        INVALID_TARGET_PERIOD("Invalid target period data"),
        INVALID_TARGET_PERIOD_DECLARED("Invalid target period declared"),

        INVALID_FACILITIES("Should include at least one facility"),
        INVALID_FACILITY_ID("Facility ID not in compliance with status"),
        INVALID_UNIQUE_FACILITY_ID("Facility ID should be unique"),
        INVALID_PREVIOUS_FACILITY_ID("Previous facility ID not exists"),
        INVALID_ADJACENT_FACILITY_ID("Adjacent facility ID not exists"),
        INVALID_FACILITY_TARGETS("Facility target improvements with scheme version are not mutually valid"),
        INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_FOR_CURRENT_SCHEME("Facility participating scheme versions with current scheme version are not mutually valid"),
        INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_ON_OWNERSHIP_CHANGE("Facility participating scheme versions with ownership change are not mutually valid"),

        INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT("Variable year energy by product should have at least one non EXCLUDED product"),
        INVALID_FACILITY_ENERGY_CONSUMPTION_BY_PRODUCT_STATUS("Product status for by product year energy is not valid"),
        INVALID_UNIQUE_PRODUCT_NAME("Product names should be unique"),

        INVALID_UNDERLYING_AGREEMENT_ACTIVATION_DETAILS_DATA("Invalid underlying agreement activation details data");

        private final String message;

        UnderlyingAgreementViolationMessage(String message) {
            this.message = message;
        }
    }
}

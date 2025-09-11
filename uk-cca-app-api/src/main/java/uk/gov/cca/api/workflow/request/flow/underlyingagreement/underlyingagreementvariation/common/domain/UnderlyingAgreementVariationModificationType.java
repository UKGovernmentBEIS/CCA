package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnderlyingAgreementVariationModificationType {

    //Target Unit and Facilities changes
    AMEND_OPERATOR_OR_ORGANISATION_NAME("Amend the name of the operator/organisation"),
    AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS("Amend the target unit address of the operator/organisation"),
    AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS("Amend the personal details for the Responsible Person (name, postal address, email address)"),
    AMEND_ONE_OR_MORE_FACILITIES_NAME("Amend the name of one or more facilities"),
    ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT("Add one or more facilities to the agreement"),
    REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT("Remove one or more facilities from the agreement"),
    AMEND_70_PERCENT_RULE_EVALUATION("Amend the 70% rule evaluation for one or more facilities"),

    //Amend the baseline and target due to
    STRUCTURAL_CHANGE("a structural change"),
    REVIEW_OF_70_PERCENT_RULE("a review of the 70% rule"),
    ERROR_DISCOVERY("the discovery of an error"),
    ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT("the addition or removal of one or more facilities from the agreement"),
    REPLACING_ESTIMATED_WITH_ACTUAL_VALUES("replacing estimated with actual values (for greenfield facilities)"),
    UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD("an unexpected power supply disruption during a target period"),
    THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD("throughput dropping by more than 10% during a target period (for absolute target types only)"),

    //Amend the target currency to
    CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPES("change between relative and Novem target types"),
    CHANGE_THROUGHPUT_UNIT("change the throughput unit"),

    //Other
    ANY_CHANGES_NOT_COVERED("Any changes not covered by the above");
    ;

    private String description;
}

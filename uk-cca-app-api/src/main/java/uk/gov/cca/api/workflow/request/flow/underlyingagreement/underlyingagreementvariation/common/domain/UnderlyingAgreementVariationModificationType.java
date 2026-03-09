package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnderlyingAgreementVariationModificationType {

    // Changes that usually require the operator to provide their assent
    AMEND_OPERATOR_OR_ORGANISATION_NAME("Amend the name of the operator/organisation"),
    AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS("Amend the address of the operator/organisation"),
    AMEND_OPERATOR_OR_ORGANISATION_COMPANY_NUMBER("Amend the company number of the operator/organisation (please provide details of the change below to allow the regulator to make the change on your behalf)"),
    AMEND_ONE_OR_MORE_FACILITIES_NAME("Amend the name of one or more facilities"),
    ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT("Add one or more facilities to the agreement"),
    REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT("Remove one or more facilities from the agreement"),
    AMEND_ONE_OR_MORE_FACILITIES_UK_ETS("Amend the UK ETS identifiers for one or more facilities"),
    AMEND_ONE_OR_MORE_IMPROVEMENT_TARGETS("Amend the improvement targets for one or more facilities due to an error"),
    UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD("Amend the improvement targets for one or more facilities due to an unexpected power supply disruption during a target period. You must notify us of this variation by 31 January in the year after the end of the target period"),
    THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD("Amend the improvement targets for one or more facilities due to throughput dropping by more than 10% during a target period. You must notify us of this variation by 31 January in the year after the end of the target period"),

    // Changes that don’t usually require the operator to provide their assent
    AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS("Amend the personal information (name, postal address, email address) for the Responsible Person"),
    AMEND_EVIDENCE_DEFINING_EXTENT_OF_FACILITY_ELIGIBILITY("Amend the evidence defining the extent of a facility’s eligibility"),
    AMEND_70_PERCENT_RULE_EVALUATION("Amend the 70% rule evaluation for one or more facilities"),
    STRUCTURAL_CHANGE("Amend a facility’s base year data due to a structural change"),
    REVIEW_OF_70_PERCENT_RULE("Amend a facility’s base year data due to a review of the 70% rule"),
    ERROR_DISCOVERY("Amend a facility’s base year data due to the discovery of an error"),
    REPLACING_ESTIMATED_WITH_ACTUAL_VALUES("Amend a facility’s base year data to replace estimated with actual values (for greenfield facilities)"),
    AMEND_FACILITY_BASE_YEAR_CHANGE_ALLOCATION("Amend a facility’s base year data to change the allocation between FIXED and/or VARIABLE amounts"),
    AMEND_FACILITY_BASE_YEAR_ADD_NEW_PRODUCTS("Amend a facility’s base year data to add new products not in the facility’s base year"),

    // Other
    ANY_CHANGES_NOT_COVERED("Any changes not covered by the above"),

    // Not to be used
    ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT("Amend the baseline and target due to the addition or removal of one or more facilities from the agreement"),
    CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPES("Amend the target currency to change between relative and Novem target types"),
    CHANGE_THROUGHPUT_UNIT("Amend the target currency to change the throughput unit"),
    ;

    private String description;
}

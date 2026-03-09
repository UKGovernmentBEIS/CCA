import { VariationChangesTypePipe } from './variation-changes-type.pipe';

describe('VariationChangesTypePipe', () => {
  it('should create an instance', () => {
    const pipe = new VariationChangesTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform variation change type', () => {
    const pipe = new VariationChangesTypePipe();

    // Require operator's assent
    expect(pipe.transform('AMEND_OPERATOR_OR_ORGANISATION_NAME')).toEqual(
      'Amend the name of the operator/organisation',
    );

    expect(pipe.transform('AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS')).toEqual(
      'Amend the address of the operator/organisation',
    );

    expect(pipe.transform('AMEND_OPERATOR_OR_ORGANISATION_COMPANY_NUMBER')).toEqual(
      'Amend the company number of the operator/organisation',
    );

    expect(pipe.transform('AMEND_ONE_OR_MORE_FACILITIES_NAME')).toEqual('Amend the name of one or more facilities');

    expect(pipe.transform('ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT')).toEqual(
      'Add one or more facilities to the agreement',
    );

    expect(pipe.transform('REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT')).toEqual(
      'Remove one or more facilities from the agreement',
    );

    expect(pipe.transform('AMEND_ONE_OR_MORE_FACILITIES_UK_ETS')).toEqual(
      'Amend the UK ETS identifiers for one or more facilities',
    );

    expect(pipe.transform('AMEND_ONE_OR_MORE_IMPROVEMENT_TARGETS')).toEqual(
      'Amend the improvement targets for one or more facilities due to an error',
    );

    expect(pipe.transform('UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD')).toEqual(
      'Amend the improvement targets for one or more facilities due to an unexpected power supply disruption during a target period',
    );

    expect(pipe.transform('THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD')).toEqual(
      'Amend the improvement targets for one or more facilities due to throughput dropping by more than 10% during a target period',
    );

    // Don't require operator's assent
    expect(pipe.transform('AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS')).toEqual(
      'Amend the personal information (name, postal address, email address) for the Responsible person',
    );

    expect(pipe.transform('AMEND_EVIDENCE_DEFINING_EXTENT_OF_FACILITY_ELIGIBILITY')).toEqual(
      "Amend the evidence defining the extend of a facility's eligibility",
    );

    expect(pipe.transform('AMEND_70_PERCENT_RULE_EVALUATION')).toEqual(
      'Amend the 70% rule evaluation for one or more facilities',
    );

    expect(pipe.transform('STRUCTURAL_CHANGE')).toEqual("Amend a facility's base year data due to a structural change");

    expect(pipe.transform('REVIEW_OF_70_PERCENT_RULE')).toEqual(
      "Amend a facility's base year data due to a review of the 70% rule",
    );

    expect(pipe.transform('ERROR_DISCOVERY')).toEqual(
      "Amend a facility's base year data due to the discovery of an error",
    );

    expect(pipe.transform('REPLACING_ESTIMATED_WITH_ACTUAL_VALUES')).toEqual(
      "Amend a facility's base year data to replace estimated with actual values (for greenfield facilities)",
    );

    expect(pipe.transform('AMEND_FACILITY_BASE_YEAR_CHANGE_ALLOCATION')).toEqual(
      "Amend a facility's base year data to change the allocation between FIXED and/or VARIABLE amounts",
    );

    expect(pipe.transform('AMEND_FACILITY_BASE_YEAR_ADD_NEW_PRODUCTS')).toEqual(
      "Amend a facility's base year data to add new products not in the facility's base year",
    );

    // Other
    expect(pipe.transform('ANY_CHANGES_NOT_COVERED')).toEqual('Any changes not covered by the above');

    // Deprecated
    expect(pipe.transform('ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT')).toEqual(
      'Amend the baseline and target due to the addition or removal of one or more facilities from the agreement',
    );

    expect(pipe.transform('CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPE')).toEqual(
      'Amend the target currency to change between relative and Novem target types',
    );

    expect(pipe.transform('CHANGE_THROUGHPUT_UNIT')).toEqual('Amend the target currency to change the throughput unit');
  });
});

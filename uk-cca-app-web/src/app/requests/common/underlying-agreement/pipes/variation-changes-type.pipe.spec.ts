import { VariationChangesTypePipe } from './variation-changes-type.pipe';

describe('VariationChangesTypePipe', () => {
  it('create an instance', () => {
    const pipe = new VariationChangesTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform variation change type', () => {
    const pipe = new VariationChangesTypePipe();

    expect(pipe.transform('AMEND_OPERATOR_OR_ORGANISATION_NAME')).toEqual(
      'Amend the name of the operator/organisation',
    );
    expect(pipe.transform('AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS')).toEqual(
      'Amend the target unit address of the operator/organisation',
    );
    expect(pipe.transform('AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS')).toEqual(
      'Amend the personal details for the Responsible Person (name, postal address, email address)',
    );
    expect(pipe.transform('AMEND_ONE_OR_MORE_FACILITIES_NAME')).toEqual('Amend the name of one or more facilities');
    expect(pipe.transform('ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT')).toEqual(
      'Add one or more facilities to the agreement',
    );
    expect(pipe.transform('REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT')).toEqual(
      'Remove one or more facilities from the agreement',
    );
    expect(pipe.transform('AMEND_70_PERCENT_RULE_EVALUATION')).toEqual(
      'Amend the 70% rule evaluation for one or more facilities',
    );

    expect(pipe.transform('STRUCTURAL_CHANGE')).toEqual('a structural change');
    expect(pipe.transform('REVIEW_OF_70_PERCENT_RULE')).toEqual('a review of the 70% rule');
    expect(pipe.transform('ERROR_DISCOVERY')).toEqual('the discovery of an error');
    expect(pipe.transform('ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT')).toEqual(
      'the addition or removal of one or more facilities from the agreement',
    );
    expect(pipe.transform('REPLACING_ESTIMATED_WITH_ACTUAL_VALUES')).toEqual(
      'replacing estimated with actual values (for greenfield facilities)',
    );
    expect(pipe.transform('UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD')).toEqual(
      'an unexpected power supply disruption during a target period',
    );
    expect(pipe.transform('THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD')).toEqual(
      'throughput dropping by more than 10% during a target period (for absolute target types only)',
    );

    expect(pipe.transform('CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPES')).toEqual(
      'change between relative and Novem target types',
    );
    expect(pipe.transform('CHANGE_THROUGHPUT_UNIT')).toEqual('change the throughput unit');

    expect(pipe.transform('ANY_CHANGES_NOT_COVERED')).toEqual('Any changes not covered by the above');
  });
});

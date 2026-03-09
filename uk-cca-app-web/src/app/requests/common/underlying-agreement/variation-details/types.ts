import { UnderlyingAgreementVariationDetails } from 'cca-api';

export const requireOperatorAssentTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'AMEND_OPERATOR_OR_ORGANISATION_NAME',
  'AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS',
  'AMEND_OPERATOR_OR_ORGANISATION_COMPANY_NUMBER',
  'AMEND_ONE_OR_MORE_FACILITIES_NAME',
  'ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT',
  'REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT',
  'AMEND_ONE_OR_MORE_FACILITIES_UK_ETS',
  'AMEND_ONE_OR_MORE_IMPROVEMENT_TARGETS',
  'UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD',
  'THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD',
];

export const dontRequireOperatorAssentTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS',
  'AMEND_EVIDENCE_DEFINING_EXTENT_OF_FACILITY_ELIGIBILITY',
  'AMEND_70_PERCENT_RULE_EVALUATION',
  'STRUCTURAL_CHANGE',
  'REVIEW_OF_70_PERCENT_RULE',
  'ERROR_DISCOVERY',
  'REPLACING_ESTIMATED_WITH_ACTUAL_VALUES',
  'AMEND_FACILITY_BASE_YEAR_CHANGE_ALLOCATION',
  'AMEND_FACILITY_BASE_YEAR_ADD_NEW_PRODUCTS',
];

export const requireOperatorAssentTypesWithHint: { value: string; hint?: string }[] = requireOperatorAssentTypes.map(
  (c) => {
    if (['AMEND_OPERATOR_OR_ORGANISATION_COMPANY_NUMBER'].includes(c)) {
      return {
        value: c,
        hint: 'Provide details of the change below to allow the regulator to make the change on your behalf',
      };
    }

    if (
      [
        'UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD',
        'THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD',
      ].includes(c)
    ) {
      return {
        value: c,
        hint: 'You must notify us of this variation by 31 January in the year after the end of the target period',
      };
    }

    return {
      value: c,
    };
  },
);

export const deprecatedChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT', // DEPRECATED
  'CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPES', // DEPRECATED
  'CHANGE_THROUGHPUT_UNIT', // DEPRECATED
];

export const otherChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = ['ANY_CHANGES_NOT_COVERED'];

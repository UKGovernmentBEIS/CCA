import { mockTargetUnitDetails, mockUnderlyingAgreement } from '@requests/common';

import { UnderlyingAgreementReviewRequestTaskPayload } from 'cca-api';

import { createProposedUnderlyingAgreementPayload } from './utils';

const basePayload: UnderlyingAgreementReviewRequestTaskPayload = {
  underlyingAgreement: mockUnderlyingAgreement,
  reviewGroupDecisions: {
    TARGET_UNIT_DETAILS: { type: 'ACCEPTED' },
    TARGET_PERIOD5_DETAILS: { type: 'REJECTED' },
    TARGET_PERIOD6_DETAILS: { type: 'REJECTED' },
    AUTHORISATION_AND_ADDITIONAL_EVIDENCE: { type: 'ACCEPTED', details: {} },
  },
  facilitiesReviewGroupDecisions: {
    'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' } },
    'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' } },
  },
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
};

describe('createProposedUnderlyingAgreementPayload', () => {
  it('should handle mixed decisions correctly', () => {
    const result = createProposedUnderlyingAgreementPayload(basePayload);
    expect(result).toMatchSnapshot('mixed-decisions');
  });

  it('should create the correct payload when all decisions are rejected', () => {
    const payload: UnderlyingAgreementReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'REJECTED', details: { notes: 'asd' } },
        'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' } },
      },
    };

    const result = createProposedUnderlyingAgreementPayload(payload);
    expect(result).toMatchSnapshot('una-without-facilities');
  });

  it('should create the correct payload when all decisions are accepted', () => {
    const payload: UnderlyingAgreementReviewRequestTaskPayload = {
      ...basePayload,

      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' } },
        'ADS_1-F00002': { type: 'ACCEPTED', details: { notes: 'asd' } },
      },
    };

    const result = createProposedUnderlyingAgreementPayload(payload);
    expect(result).toMatchSnapshot('una-with-initial-facilities');
  });

  it('should transform target unit details when TARGET_UNIT_DETAILS is REJECTED', () => {
    const payload: UnderlyingAgreementReviewRequestTaskPayload = {
      ...basePayload,
      reviewGroupDecisions: {
        TARGET_UNIT_DETAILS: { type: 'REJECTED' },
      },
    };

    const result = createProposedUnderlyingAgreementPayload(payload);
    expect(result).toMatchSnapshot('target-unit-details-rejected');
  });
});

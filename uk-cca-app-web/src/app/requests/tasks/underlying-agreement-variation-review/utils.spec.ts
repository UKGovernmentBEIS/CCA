import { mockTargetUnitDetails } from '@requests/common';

import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

import {
  mockOriginalUnderlyingAgreementContainer,
  mockUnderlyingAgreementVariation,
} from '../underlying-agreement-variation/testing/mock-data';
import { createProposedUnderlyingAgreementVariationPayload } from './utils';

const basePayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
  originalUnderlyingAgreementContainer: mockOriginalUnderlyingAgreementContainer,
  underlyingAgreement: mockUnderlyingAgreementVariation,
  reviewGroupDecisions: {
    VARIATION_DETAILS: { type: 'REJECTED', details: {} },
    TARGET_UNIT_DETAILS: { type: 'ACCEPTED' },
    TARGET_PERIOD5_DETAILS: { type: 'REJECTED' },
    TARGET_PERIOD6_DETAILS: { type: 'REJECTED' },
    AUTHORISATION_AND_ADDITIONAL_EVIDENCE: { type: 'ACCEPTED', details: {} },
  },
  facilitiesReviewGroupDecisions: {
    'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
    'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
    'ADS_1-F00003': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
    'ADS_1-F00004': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
  },
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      measurementType: 'ENERGY_KWH',
    },
  },
};

describe('createProposedUnderlyingAgreementVariationPayload', () => {
  it('should handle mixed decisions correctly', () => {
    const result = createProposedUnderlyingAgreementVariationPayload(basePayload);
    expect(result).toMatchSnapshot('mixed-decisions');
  });

  it('should create the correct payload when all decisions are rejected', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewGroupDecisions: {
        VARIATION_DETAILS: { type: 'REJECTED', details: {} },
        TARGET_UNIT_DETAILS: { type: 'REJECTED' },
        TARGET_PERIOD5_DETAILS: { type: 'REJECTED' },
        TARGET_PERIOD6_DETAILS: { type: 'REJECTED' },
        AUTHORISATION_AND_ADDITIONAL_EVIDENCE: { type: 'REJECTED', details: {} },
      },
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00003': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
        'ADS_1-F00004': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);
    expect(result).toMatchSnapshot('all-decisions-rejected');
  });

  it('should create the correct payload when all decisions are approved', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewGroupDecisions: {
        VARIATION_DETAILS: { type: 'ACCEPTED', details: {} },
        TARGET_UNIT_DETAILS: { type: 'ACCEPTED' },
        TARGET_PERIOD5_DETAILS: { type: 'ACCEPTED' },
        TARGET_PERIOD6_DETAILS: { type: 'ACCEPTED' },
        AUTHORISATION_AND_ADDITIONAL_EVIDENCE: { type: 'ACCEPTED', details: {} },
      },
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00002': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00003': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
        'ADS_1-F00004': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);
    expect(result).toMatchSnapshot('all-decisions-accepted');
  });

  it('should transform target unit details when TARGET_UNIT_DETAILS is REJECTED', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewGroupDecisions: {
        TARGET_UNIT_DETAILS: { type: 'REJECTED' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);
    expect(result).toMatchSnapshot('target-unit-details-rejected');
  });
});

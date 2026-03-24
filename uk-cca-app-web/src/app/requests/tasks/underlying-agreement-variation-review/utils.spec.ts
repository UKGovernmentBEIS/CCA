import { mockTargetUnitDetails, transformAccountReferenceData } from '@requests/common';
import { SchemeVersion } from '@shared/types';

import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

import {
  mockMalformedOriginalUnderlyingAgreementContainer,
  mockMalformedUnderlyingAgreementVariation,
  mockOriginalUnderlyingAgreementContainer,
  mockUnderlyingAgreementVariation,
} from '../underlying-agreement-variation/testing/mock-data';
import { createProposedUnderlyingAgreementVariationPayload } from './utils';

const basePayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
  originalUnderlyingAgreementContainer: mockOriginalUnderlyingAgreementContainer,
  underlyingAgreement: mockUnderlyingAgreementVariation,
  reviewSectionsCompleted: { 'ADS_1-F00005': 'UNCHANGED' },
  reviewGroupDecisions: {
    VARIATION_DETAILS: { type: 'REJECTED', details: {} },
    TARGET_UNIT_DETAILS: { type: 'ACCEPTED' },
    TARGET_PERIOD5_DETAILS: { type: 'REJECTED' },
    TARGET_PERIOD6_DETAILS: { type: 'REJECTED' },
    AUTHORISATION_AND_ADDITIONAL_EVIDENCE: { type: 'ACCEPTED', details: {} },
  },
  facilitiesReviewGroupDecisions: {
    'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
    'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
    'ADS_1-F00003': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
    'ADS_1-F00004': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
  },
  accountReferenceData: {
    targetUnitAccountDetails: mockTargetUnitDetails,
    sectorAssociationDetails: {
      subsectorAssociationName: 'SUBSECTOR_2',
      schemeDataMap: {
        [SchemeVersion.CCA_2]: { sectorMeasurementType: 'ENERGY_KWH', sectorThroughputUnit: 'tonne' },
        [SchemeVersion.CCA_3]: { sectorMeasurementType: 'ENERGY_KWH' },
      },
    },
  },
};

const originalFacility = (id: string) =>
  mockOriginalUnderlyingAgreementContainer.underlyingAgreement.facilities.find((f) => f.facilityId === id);

const variationFacility = (id: string) => mockUnderlyingAgreementVariation.facilities.find((f) => f.facilityId === id);

const facilitiesIds = (result: ReturnType<typeof createProposedUnderlyingAgreementVariationPayload>) =>
  (result.facilities ?? [])
    .map((f) => f?.facilityId)
    .filter(Boolean)
    .sort();

describe('createProposedUnderlyingAgreementVariationPayload', () => {
  it('should contain mixed accepted and rejected facility outcomes', () => {
    const result = createProposedUnderlyingAgreementVariationPayload(basePayload);

    expect(result.underlyingAgreementVariationDetails).toEqual(
      mockUnderlyingAgreementVariation.underlyingAgreementVariationDetails,
    );
    expect(result.underlyingAgreementTargetUnitDetails).toEqual(
      mockUnderlyingAgreementVariation.underlyingAgreementTargetUnitDetails,
    );
    expect(result.targetPeriod5Details).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod5Details,
    );
    expect(result.targetPeriod6Details).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod6Details,
    );
    expect(result.authorisationAndAdditionalEvidence).toEqual(
      mockUnderlyingAgreementVariation.authorisationAndAdditionalEvidence,
    );

    expect(facilitiesIds(result)).toEqual(['ADS_1-F00001', 'ADS_1-F00003']);

    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00001')).toEqual(variationFacility('ADS_1-F00001'));
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(variationFacility('ADS_1-F00003'));

    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00002')).toBeUndefined();
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00004')).toBeUndefined();
  });

  it('should load rejected sections and facilities from original payload where applicable', () => {
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
        'ADS_1-F00001': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
        'ADS_1-F00002': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00003': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
        'ADS_1-F00004': { type: 'REJECTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.targetPeriod5Details).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod5Details,
    );
    expect(result.targetPeriod6Details).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod6Details,
    );
    expect(result.authorisationAndAdditionalEvidence).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.authorisationAndAdditionalEvidence,
    );
    expect(result.underlyingAgreementTargetUnitDetails).toEqual(
      transformAccountReferenceData(basePayload.accountReferenceData),
    );

    expect(facilitiesIds(result)).toEqual(['ADS_1-F00001', 'ADS_1-F00003']);
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00001')).toEqual(originalFacility('ADS_1-F00001'));
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(originalFacility('ADS_1-F00003'));
  });

  it('should keep variation data as-is when all decisions are accepted', () => {
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
        'ADS_1-F00001': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'LIVE' },
        'ADS_1-F00002': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
        'ADS_1-F00003': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'EXCLUDED' },
        'ADS_1-F00004': { type: 'ACCEPTED', details: { notes: 'asd' }, facilityStatus: 'NEW' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.targetPeriod5Details).toEqual(mockUnderlyingAgreementVariation.targetPeriod5Details);
    expect(result.targetPeriod6Details).toEqual(mockUnderlyingAgreementVariation.targetPeriod6Details);
    expect(result.authorisationAndAdditionalEvidence).toEqual(
      mockUnderlyingAgreementVariation.authorisationAndAdditionalEvidence,
    );
    expect(result.underlyingAgreementTargetUnitDetails).toEqual(
      mockUnderlyingAgreementVariation.underlyingAgreementTargetUnitDetails,
    );
    expect(facilitiesIds(result)).toEqual(['ADS_1-F00001', 'ADS_1-F00002', 'ADS_1-F00003', 'ADS_1-F00004']);
  });

  it('should return proper data for mixed group/facility decisions and unchanged variation facilities', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewSectionsCompleted: {
        ...basePayload.reviewSectionsCompleted,
        'ADS_1-F00003': 'UNCHANGED',
      },
      facilitiesReviewGroupDecisions: {
        ...basePayload.facilitiesReviewGroupDecisions,
      },
    };

    delete payload.facilitiesReviewGroupDecisions['ADS_1-F00003'];

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.authorisationAndAdditionalEvidence).toEqual(
      mockUnderlyingAgreementVariation.authorisationAndAdditionalEvidence,
    );
    expect(result.targetPeriod6Details).toEqual(
      mockOriginalUnderlyingAgreementContainer.underlyingAgreement.targetPeriod6Details,
    );
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(variationFacility('ADS_1-F00003'));
    expect(facilitiesIds(result)).toEqual(['ADS_1-F00001', 'ADS_1-F00003']);
  });

  it('should handle NEW status facilities correctly', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00002': { type: 'REJECTED', details: {}, facilityStatus: 'NEW' },
        'ADS_1-F00004': { type: 'ACCEPTED', details: {}, facilityStatus: 'NEW' },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00002')).toBeUndefined();
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00004')).toEqual(variationFacility('ADS_1-F00004'));
  });

  it('should handle LIVE status facilities correctly', () => {
    const acceptedPayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'ACCEPTED', details: {}, facilityStatus: 'LIVE' },
      },
    };

    const rejectedPayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00001': { type: 'REJECTED', details: {}, facilityStatus: 'LIVE' },
      },
    };

    const acceptedResult = createProposedUnderlyingAgreementVariationPayload(acceptedPayload);
    const rejectedResult = createProposedUnderlyingAgreementVariationPayload(rejectedPayload);

    expect(acceptedResult.facilities.find((f) => f.facilityId === 'ADS_1-F00001')).toEqual(
      variationFacility('ADS_1-F00001'),
    );
    expect(rejectedResult.facilities.find((f) => f.facilityId === 'ADS_1-F00001')).toEqual(
      originalFacility('ADS_1-F00001'),
    );
  });

  it('should handle EXCLUDED status facilities correctly', () => {
    const acceptedPayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00003': { type: 'ACCEPTED', details: {}, facilityStatus: 'EXCLUDED' },
      },
    };

    const rejectedPayload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      facilitiesReviewGroupDecisions: {
        'ADS_1-F00003': { type: 'REJECTED', details: {}, facilityStatus: 'EXCLUDED' },
      },
    };

    const acceptedResult = createProposedUnderlyingAgreementVariationPayload(acceptedPayload);
    const rejectedResult = createProposedUnderlyingAgreementVariationPayload(rejectedPayload);

    expect(acceptedResult.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(
      variationFacility('ADS_1-F00003'),
    );
    expect(rejectedResult.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(
      originalFacility('ADS_1-F00003'),
    );
  });

  it('should include unchanged facilities only if they are inside variation facilities', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewSectionsCompleted: {
        'ADS_1-F00003': 'UNCHANGED',
        'ADS_1-F00005': 'UNCHANGED',
      },
      facilitiesReviewGroupDecisions: {},
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00003')).toEqual(variationFacility('ADS_1-F00003'));
    expect(result.facilities.find((f) => f.facilityId === 'ADS_1-F00005')).toBeUndefined();
  });

  it('should include multiple unchanged facilities when they are present in variation facilities', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewSectionsCompleted: {
        'ADS_1-F00001': 'UNCHANGED',
        'ADS_1-F00003': 'UNCHANGED',
      },
      facilitiesReviewGroupDecisions: {},
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(facilitiesIds(result)).toEqual(['ADS_1-F00001', 'ADS_1-F00003']);
  });

  it('should keep variation details even when VARIATION_DETAILS decision is rejected', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      reviewGroupDecisions: {
        ...basePayload.reviewGroupDecisions,
        VARIATION_DETAILS: { type: 'REJECTED', details: { notes: 'ignored' } },
      },
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.underlyingAgreementVariationDetails).toEqual(
      mockUnderlyingAgreementVariation.underlyingAgreementVariationDetails,
    );
  });

  it('should return empty facilities when variation facilities are empty', () => {
    const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
      ...basePayload,
      underlyingAgreement: {
        ...mockUnderlyingAgreementVariation,
        facilities: [],
      },
      facilitiesReviewGroupDecisions: {},
      reviewSectionsCompleted: {},
    };

    const result = createProposedUnderlyingAgreementVariationPayload(payload);

    expect(result.facilities).toEqual([]);
  });

  describe('edge cases', () => {
    it('should duplicate facility when same facility is both decided and unchanged', () => {
      const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
        ...basePayload,
        reviewSectionsCompleted: {
          ...basePayload.reviewSectionsCompleted,
          'ADS_1-F00001': 'UNCHANGED',
        },
        facilitiesReviewGroupDecisions: {
          ...basePayload.facilitiesReviewGroupDecisions,
          'ADS_1-F00001': { type: 'ACCEPTED', details: {}, facilityStatus: 'LIVE' },
        },
      };

      const result = createProposedUnderlyingAgreementVariationPayload(payload);
      const ids = (result.facilities ?? []).map((f) => f.facilityId);

      expect(ids.filter((id) => id === 'ADS_1-F00001').length).toBe(2);
    });

    it('should transform target unit details when TARGET_UNIT_DETAILS is REJECTED', () => {
      const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
        ...basePayload,
        reviewGroupDecisions: {
          TARGET_UNIT_DETAILS: { type: 'REJECTED' },
        },
      };

      const result = createProposedUnderlyingAgreementVariationPayload(payload);

      expect(result.underlyingAgreementTargetUnitDetails).toEqual(
        transformAccountReferenceData(basePayload.accountReferenceData),
      );
    });

    it('should keep variation section data when no review group decision is provided', () => {
      const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
        ...basePayload,
        reviewGroupDecisions: {},
      };

      const result = createProposedUnderlyingAgreementVariationPayload(payload);

      expect(result.targetPeriod6Details).toEqual(mockUnderlyingAgreementVariation.targetPeriod6Details);
      expect(result.authorisationAndAdditionalEvidence).toEqual(
        mockUnderlyingAgreementVariation.authorisationAndAdditionalEvidence,
      );
    });

    it('should return empty facilities when no decisions and no unchanged facilities in variation payload', () => {
      const payload: UnderlyingAgreementVariationReviewRequestTaskPayload = {
        ...basePayload,
        facilitiesReviewGroupDecisions: {},
        reviewSectionsCompleted: {
          'ADS_1-F00005': 'UNCHANGED',
        },
      };

      const result = createProposedUnderlyingAgreementVariationPayload(payload);

      expect(result.facilities).toEqual([]);
    });

    it('should throw when TARGET_UNIT_DETAILS is rejected and accountReferenceData is missing', () => {
      const payload = {
        ...basePayload,
        accountReferenceData: undefined,
        reviewGroupDecisions: {
          TARGET_UNIT_DETAILS: { type: 'REJECTED' },
        },
      } as unknown as UnderlyingAgreementVariationReviewRequestTaskPayload;

      expect(() => createProposedUnderlyingAgreementVariationPayload(payload)).toThrow(
        'Malformed payload: accountReferenceData is required when TARGET_UNIT_DETAILS is REJECTED',
      );
    });

    it('should throw when review decision maps are missing', () => {
      const payload = {
        ...basePayload,
        reviewGroupDecisions: undefined,
        facilitiesReviewGroupDecisions: undefined,
        reviewSectionsCompleted: undefined,
      } as unknown as UnderlyingAgreementVariationReviewRequestTaskPayload;

      expect(() => createProposedUnderlyingAgreementVariationPayload(payload)).toThrow(
        'Malformed payload: reviewSectionsCompleted is required to create proposed underlying agreement payload',
      );
    });

    it('should throw when underlying agreement is malformed from API', () => {
      const payload = {
        ...basePayload,
        underlyingAgreement: mockMalformedUnderlyingAgreementVariation,
        reviewGroupDecisions: {},
        facilitiesReviewGroupDecisions: {},
        reviewSectionsCompleted: {},
      } as unknown as UnderlyingAgreementVariationReviewRequestTaskPayload;

      expect(() => createProposedUnderlyingAgreementVariationPayload(payload)).toThrow(
        'Malformed payload: underlyingAgreement.facilities is required to create proposed underlying agreement payload',
      );
    });

    it('should throw when original underlying agreement container is malformed', () => {
      const payload = {
        ...basePayload,
        originalUnderlyingAgreementContainer: mockMalformedOriginalUnderlyingAgreementContainer,
        reviewGroupDecisions: {
          TARGET_PERIOD6_DETAILS: { type: 'REJECTED' },
        },
        facilitiesReviewGroupDecisions: {
          'ADS_1-F00001': { type: 'REJECTED', details: {}, facilityStatus: 'LIVE' },
        },
      } as unknown as UnderlyingAgreementVariationReviewRequestTaskPayload;

      expect(() => createProposedUnderlyingAgreementVariationPayload(payload)).toThrow(
        'Malformed payload: originalUnderlyingAgreementContainer.underlyingAgreement.facilities is required to create proposed underlying agreement payload',
      );
    });

    it('should throw when facilitiesReviewGroupDecisions contains an unknown facility id', () => {
      const payload = {
        ...basePayload,
        facilitiesReviewGroupDecisions: {
          UNKNOWN_FACILITY: { type: 'ACCEPTED', details: {}, facilityStatus: 'LIVE' },
        },
      } as unknown as UnderlyingAgreementVariationReviewRequestTaskPayload;

      expect(() => createProposedUnderlyingAgreementVariationPayload(payload)).toThrow(
        'Malformed payload: facilitiesReviewGroupDecisions contains unknown facilityId UNKNOWN_FACILITY',
      );
    });
  });
});

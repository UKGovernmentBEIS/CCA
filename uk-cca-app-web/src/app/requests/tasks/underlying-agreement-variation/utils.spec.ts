import { signal } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import {
  areEntitiesIdentical,
  isStatusFinal,
  SUBTASK_TO_DECISION_MAP,
  TaskItemStatus,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';

import {
  extractReviewProps,
  type FacilityReviewProps,
  removeFacilityReviewSection,
  resetFacilityReviewSection,
  resetReviewSection,
  type ReviewProps,
  setVariationDetailsReviewSection,
} from './utils';

describe('Utils', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('resetReviewSection', () => {
    it('should reset review section with valid subtask', () => {
      const validSubtask = VARIATION_DETAILS_SUBTASK;
      const decisionKey = SUBTASK_TO_DECISION_MAP[validSubtask];

      const reviewProps: ReviewProps = {
        reviewSectionsCompleted: { [validSubtask]: TaskItemStatus.COMPLETED },
        reviewGroupDecisions: { [decisionKey]: { type: 'ACCEPTED' } },
      };

      const result = resetReviewSection(reviewProps, validSubtask, false);

      expect(result.reviewSectionsCompleted[validSubtask]).toBe(TaskItemStatus.UNDECIDED);
      expect(result.reviewGroupDecisions[decisionKey]).toBeUndefined();
    });

    it('should throw error for invalid subtask', () => {
      const reviewProps: ReviewProps = {
        reviewSectionsCompleted: {},
        reviewGroupDecisions: {},
      };

      expect(() => resetReviewSection(reviewProps, 'invalid-subtask', false)).toThrow(
        'Submit Variation Action - Invalid subtask decision key: invalid-subtask',
      );
    });
  });

  describe('resetFacilityReviewSection', () => {
    it('should reset facility review section', () => {
      const reviewProps: FacilityReviewProps = {
        reviewSectionsCompleted: { 'ADS_3-F00001': TaskItemStatus.COMPLETED },
        facilitiesReviewGroupDecisions: { 'ADS_3-F00001': { type: 'ACCEPTED', facilityStatus: 'LIVE' } },
      };

      const result = resetFacilityReviewSection(reviewProps, 'ADS_3-F00001', false);

      expect(result.reviewSectionsCompleted['ADS_3-F00001']).toBe(TaskItemStatus.UNDECIDED);
      expect(result.facilitiesReviewGroupDecisions['ADS_3-F00001']).toBeUndefined();
    });
  });

  describe('removeFacilityReviewSection', () => {
    it('should remove facility review section completely', () => {
      const reviewProps: FacilityReviewProps = {
        reviewSectionsCompleted: { 'ADS_3-F00001': TaskItemStatus.COMPLETED },
        facilitiesReviewGroupDecisions: { 'ADS_3-F00001': { type: 'ACCEPTED', facilityStatus: 'LIVE' } },
      };

      const result = removeFacilityReviewSection(reviewProps, 'ADS_3-F00001');

      expect(result.reviewSectionsCompleted['ADS_3-F00001']).toBeUndefined();
      expect(result.facilitiesReviewGroupDecisions['ADS_3-F00001']).toBeUndefined();
    });
  });

  describe('setVariationDetailsReviewSection', () => {
    it('should set variation details review section to accepted', () => {
      const reviewProps: ReviewProps = {
        reviewSectionsCompleted: {},
        reviewGroupDecisions: {},
      };

      const result = setVariationDetailsReviewSection(reviewProps);

      const decisionKey = SUBTASK_TO_DECISION_MAP[VARIATION_DETAILS_SUBTASK];
      expect(result.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK]).toBe(TaskItemStatus.ACCEPTED);
      expect(result.reviewGroupDecisions[decisionKey]).toEqual({ type: 'ACCEPTED' });
    });
  });

  describe('extractReviewProps', () => {
    let mockStore: jest.Mocked<RequestTaskStore>;

    beforeEach(() => {
      mockStore = {
        select: jest.fn(),
      } as unknown as jest.Mocked<RequestTaskStore>;
    });

    it('should extract review props from store', () => {
      mockStore.select.mockReturnValue(signal({}));

      const result = extractReviewProps(mockStore);

      expect(result).toEqual({
        reviewSectionsCompleted: {},
        reviewGroupDecisions: {},
        facilitiesReviewGroupDecisions: {},
      });
      expect(mockStore.select).toHaveBeenCalledWith(underlyingAgreementReviewQuery.selectReviewSectionsCompleted);
      expect(mockStore.select).toHaveBeenCalledWith(underlyingAgreementVariationQuery.selectReviewGroupDecisions);
      expect(mockStore.select).toHaveBeenCalledWith(
        underlyingAgreementVariationQuery.selectFacilityReviewGroupDecisions,
      );
    });

    it('should handle null values from store selectors', () => {
      mockStore.select.mockReturnValue(signal(null));

      const result = extractReviewProps(mockStore);

      expect(result).toEqual({
        reviewSectionsCompleted: {},
        reviewGroupDecisions: {},
        facilitiesReviewGroupDecisions: {},
      });
    });
  });

  describe('areEntitiesIdentical', () => {
    it('should return true for identical primitives', () => {
      expect(areEntitiesIdentical(1, 1)).toBe(true);
      expect(areEntitiesIdentical('test', 'test')).toBe(true);
      expect(areEntitiesIdentical(true, true)).toBe(true);
    });

    it('should return false for different primitives', () => {
      expect(areEntitiesIdentical(1, 2)).toBe(false);
      expect(areEntitiesIdentical('test', 'different')).toBe(false);
      expect(areEntitiesIdentical(true, false)).toBe(false);
    });

    it('should return true for same reference', () => {
      const obj = { a: 1 };
      expect(areEntitiesIdentical(obj, obj)).toBe(true);
    });

    it('should return false for null/undefined comparisons', () => {
      expect(areEntitiesIdentical(null, {})).toBe(false);
      expect(areEntitiesIdentical({}, null)).toBe(false);
      expect(areEntitiesIdentical(undefined, {})).toBe(false);
    });

    it('should return true for identical objects', () => {
      const obj1 = { a: 1, b: 'test', c: { nested: true } };
      const obj2 = { a: 1, b: 'test', c: { nested: true } };
      expect(areEntitiesIdentical(obj1, obj2)).toBe(true);
    });

    it('should return false for different objects', () => {
      const obj1 = { a: 1, b: 'test', c: 7 };
      const obj2 = { a: 2, b: 'test', c: 7 };
      const obj3 = { a: 1, b: 'test', c: { d: 2 } };
      expect(areEntitiesIdentical(obj1, obj2)).toBe(false);
      expect(areEntitiesIdentical(obj1, obj3)).toBe(false);
    });

    it('should return false for objects with different key counts', () => {
      const obj1 = { a: 1, b: 2 };
      const obj2 = { a: 1 };
      expect(areEntitiesIdentical(obj1, obj2)).toBe(false);
    });
    it('should handle nested objects recursively', () => {
      const obj1 = { a: { b: { c: 1 } } };
      const obj2 = { a: { b: { c: 1 } } };
      const obj3 = { a: { b: { c: 2 } } };

      expect(areEntitiesIdentical(obj1, obj2)).toBe(true);
      expect(areEntitiesIdentical(obj1, obj3)).toBe(false);
    });
  });

  describe('isStatusFinal', () => {
    it('should return true for COMPLETED status', () => {
      expect(isStatusFinal(TaskItemStatus.COMPLETED)).toBe(true);
    });

    it('should return true for UNCHANGED status', () => {
      expect(isStatusFinal(TaskItemStatus.UNCHANGED)).toBe(true);
    });

    it('should return false for other statuses', () => {
      expect(isStatusFinal(TaskItemStatus.UNDECIDED)).toBe(false);
      expect(isStatusFinal(TaskItemStatus.ACCEPTED)).toBe(false);
      expect(isStatusFinal('OTHER_STATUS')).toBe(false);
    });
  });
});

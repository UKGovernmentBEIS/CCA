import { TargetPeriodType } from '@shared/types';

import { TargetPeriodBuyOutDetailsDTO } from 'cca-api';

import { buyOutRunCreateActionFor, sortTargetPeriodsDesc } from './utils';

describe('sortTargetPeriodsDesc', () => {
  it('should put the latest target period first, so that it becomes the default of the batch', () => {
    const targetPeriods: TargetPeriodBuyOutDetailsDTO[] = [
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
      { id: 9, businessId: 'TP9', buyOutCost: 41 },
      { id: 8, businessId: 'TP8', buyOutCost: 38 },
    ];

    expect(sortTargetPeriodsDesc(targetPeriods).map(({ businessId }) => businessId)).toEqual(['TP9', 'TP8', 'TP7']);
  });

  it('should order the overlapping target periods so that the CCA3 one wins', () => {
    const targetPeriods: TargetPeriodBuyOutDetailsDTO[] = [
      { id: 6, businessId: 'TP6' },
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
    ];

    expect(sortTargetPeriodsDesc(targetPeriods)[0].businessId).toBe('TP7');
  });

  it('should drop entries the API returned without a business id', () => {
    const targetPeriods: TargetPeriodBuyOutDetailsDTO[] = [
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
      { id: 8, buyOutCost: 38 },
    ];

    expect(sortTargetPeriodsDesc(targetPeriods).map(({ businessId }) => businessId)).toEqual(['TP7']);
  });

  it('should return an empty list when the target periods are missing', () => {
    expect(sortTargetPeriodsDesc()).toEqual([]);
    expect(sortTargetPeriodsDesc(undefined)).toEqual([]);
    expect(sortTargetPeriodsDesc([])).toEqual([]);
  });

  it('should not mutate the list it was given', () => {
    const targetPeriods: TargetPeriodBuyOutDetailsDTO[] = [
      { id: 7, businessId: 'TP7', buyOutCost: 37 },
      { id: 9, businessId: 'TP9', buyOutCost: 41 },
    ];

    sortTargetPeriodsDesc(targetPeriods);

    expect(targetPeriods.map(({ businessId }) => businessId)).toEqual(['TP7', 'TP9']);
  });
});

describe('buyOutRunCreateActionFor', () => {
  const cca2Action = {
    requestType: 'BUY_OUT_SURPLUS_RUN',
    payloadType: 'BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD',
  };

  const cca3Action = {
    requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN',
    payloadType: 'BUY_OUT_SURPLUS_FACILITY_RUN_CREATE_ACTION_PAYLOAD',
  };

  it('should trigger the workflow that matches the scheme of the target period', () => {
    const testCases: { targetPeriod: TargetPeriodType; expected: typeof cca2Action }[] = [
      { targetPeriod: 'TP5', expected: cca2Action },
      { targetPeriod: 'TP6', expected: cca2Action },
      { targetPeriod: 'TP7', expected: cca3Action },
      { targetPeriod: 'TP8', expected: cca3Action },
      { targetPeriod: 'TP9', expected: cca3Action },
    ];

    testCases.forEach(({ targetPeriod, expected }) => {
      expect(buyOutRunCreateActionFor(targetPeriod)).toEqual(expected);
    });
  });
});

import { compareTargetPeriodsDesc, isCCA3TargetPeriod, TargetPeriodType } from '@shared/types';

import { TargetPeriodBuyOutDetailsDTO } from 'cca-api';

export type TargetPeriodBuyOutDetails = TargetPeriodBuyOutDetailsDTO & { businessId: TargetPeriodType };

export type BuyOutRunCreateAction = {
  requestType: string;
  payloadType: string;
};

/**
 * Orders the available target periods from the latest to the earliest, dropping any entry the API returned
 * without a business id, so that the head of the list is always the target period the batch defaults to.
 *
 * Note that the API orders the current target periods with TP6 first during an overlap, whereas the batch
 * defaults to the CCA3 one. Do not replace this with the order the API returned.
 */
export const sortTargetPeriodsDesc = (
  targetPeriods: TargetPeriodBuyOutDetailsDTO[] = [],
): TargetPeriodBuyOutDetails[] =>
  targetPeriods
    .filter((targetPeriod): targetPeriod is TargetPeriodBuyOutDetails => !!targetPeriod.businessId)
    .sort((a, b) => compareTargetPeriodsDesc(a.businessId, b.businessId));

/**
 * CCA2 and CCA3 run as two separate workflows, calculating at target unit and at facility level respectively.
 * The selected target period decides which one the batch triggers.
 */
export const buyOutRunCreateActionFor = (targetPeriod: TargetPeriodType): BuyOutRunCreateAction =>
  isCCA3TargetPeriod(targetPeriod)
    ? {
        requestType: 'BUY_OUT_SURPLUS_FACILITY_RUN',
        payloadType: 'BUY_OUT_SURPLUS_FACILITY_RUN_CREATE_ACTION_PAYLOAD',
      }
    : {
        requestType: 'BUY_OUT_SURPLUS_RUN',
        payloadType: 'BUY_OUT_SURPLUS_RUN_CREATE_ACTION_PAYLOAD',
      };

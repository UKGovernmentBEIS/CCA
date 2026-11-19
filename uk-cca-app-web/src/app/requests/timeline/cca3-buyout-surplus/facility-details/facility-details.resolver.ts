import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, RedirectCommand, ResolveFn, Router } from '@angular/router';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';

import { BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

import { HistoricalFacility, toHistoricalFacilities } from '../historical-facilities';
import { FacilityCalculationPresentation } from './facility-calculation.types';
import { toFacilityCalculationPresentation } from './facility-calculation-presentation';

export interface HistoricalFacilityDetails {
  actionId: number;
  creationDate: string;
  runId: string;
  facility: HistoricalFacility;
}

export function findHistoricalFacility(
  action: RequestActionDTO | null,
  actionId: string | null,
  facilityId: string | null,
  targetPeriod: string | null,
): HistoricalFacilityDetails | undefined {
  if (
    !actionId ||
    !facilityId ||
    !targetPeriod ||
    String(action?.id) !== actionId ||
    action?.type !== 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED' ||
    action.payload?.payloadType !== 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD'
  ) {
    return undefined;
  }

  const payload = action.payload as BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload;
  const matches = toHistoricalFacilities(payload).filter(
    (facility) => facility.facilityBusinessId === facilityId && facility.targetPeriod === targetPeriod,
  );
  if (matches?.length !== 1 || !['TARGET_MET', 'TARGET_NOT_MET'].includes(matches[0].performanceOutcome)) {
    return undefined;
  }

  return { actionId: action.id, creationDate: action.creationDate, runId: payload.runId, facility: matches[0] };
}

function resolveHistoricalFacility(route: ActivatedRouteSnapshot): HistoricalFacilityDetails | undefined {
  const action = inject(RequestActionStore).select(requestActionQuery.selectAction)();
  const actionId = route.pathFromRoot.find((parent) => parent.paramMap.has('actionId'))?.paramMap.get('actionId');
  return findHistoricalFacility(action, actionId, route.paramMap.get('facilityId'), route.paramMap.get('targetPeriod'));
}

export const facilityDetailsResolver: ResolveFn<HistoricalFacilityDetails> = (route) =>
  resolveHistoricalFacility(route) ?? new RedirectCommand(inject(Router).parseUrl('/error/404'));

export const facilityCalculationResolver: ResolveFn<FacilityCalculationPresentation> = (route) => {
  const details = resolveHistoricalFacility(route);
  const calculation = details && toFacilityCalculationPresentation(details.facility);
  return calculation ?? new RedirectCommand(inject(Router).parseUrl('/error/404'));
};

// Titles are resolved alongside data, so cannot read the sibling resolver's result yet.
export const facilityDetailsTitleResolver: ResolveFn<string> = (route) =>
  resolveHistoricalFacility(route)?.facility.siteName ?? 'Facility details';

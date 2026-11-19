import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { etsReportingQuery } from '../../ets-reporting.selectors';
import { ETS_ENERGY_FUEL_DETAILS_SUBTASK } from '../../ets-reporting.types';
import {
  areEtsUseAnswersComplete,
  isEtsReportingComplete,
  requiresEtsFuelDetails,
} from '../../ets-reporting.validation';

export const etsEnergyFuelAmountRedirectGuard: CanActivateFn = (route) => {
  const payload = inject(RequestTaskStore).select(etsReportingQuery.selectPayload)();
  const input = payload?.emissionsTradingSchemeDataInputDetails;
  let step: string;
  if (!areEtsUseAnswersComplete(input) || (input.isUltraSmallEmitter === false && input.provideFuelDetails != null)) {
    step = 'ultra-small-emitter';
  } else if (!isEtsReportingComplete(payload)) {
    step = requiresEtsFuelDetails(input) ? 'details' : 'ultra-small-emitter';
  } else {
    step =
      payload?.sectionsCompleted?.[ETS_ENERGY_FUEL_DETAILS_SUBTASK] === 'COMPLETED' ? 'summary' : 'check-your-answers';
  }
  return createUrlTreeFromSnapshot(route, [step]);
};

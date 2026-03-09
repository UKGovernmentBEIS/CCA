import { Improvement } from '@shared/types';
import { produce } from 'immer';

import { FacilityTargets } from 'cca-api';

import { FacilityPayload } from '../types';
import { FacilityTargetsFormModel } from './targets-form.provider';

export function updateFacilityTargets(
  payload: FacilityPayload,
  form: FacilityTargetsFormModel,
  facilityId: string,
): FacilityPayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const improvements: FacilityTargets['improvements'] = {
      [Improvement.TP7]: String(form.value.tp7),
      [Improvement.TP8]: String(form.value.tp8),
      [Improvement.TP9]: String(form.value.tp9),
    };

    draft.facilities[facilityIndex].cca3BaselineAndTargets.facilityTargets = {
      improvements,
    };
  });
}

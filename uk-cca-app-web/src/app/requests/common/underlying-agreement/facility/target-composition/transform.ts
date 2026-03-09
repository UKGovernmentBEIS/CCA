import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { FacilityTargetCompositionFormModel } from '../../target-periods';
import { FacilityPayload } from '../types';

export function updateFacilityTargetComposition(
  payload: FacilityPayload,
  form: FacilityTargetCompositionFormModel,
  facilityId: string,
): FacilityPayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    if (draft.facilities[facilityIndex]?.cca3BaselineAndTargets?.targetComposition) {
      draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets.targetComposition,
        ...form.value,
        calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
      };
    } else {
      draft.facilities[facilityIndex].cca3BaselineAndTargets = {
        ...draft.facilities[facilityIndex].cca3BaselineAndTargets,
        targetComposition: {
          calculatorFile: fileUtils.toUUIDs([form.value.calculatorFile])[0] || '',
          measurementType: form.value.measurementType,
          agreementCompositionType: form.value.agreementCompositionType ?? 'NOVEM',
        },
      };
    }
  });
}

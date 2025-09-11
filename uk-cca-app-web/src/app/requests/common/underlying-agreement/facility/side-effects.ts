import { produce } from 'immer';

import type { UnderlyingAgreementApplySavePayload } from 'cca-api';

import { isCCA3Scheme } from '../../utils';

export function applySchemeVersionsSideEffect(
  payload: UnderlyingAgreementApplySavePayload,
  facilityId: string,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    const facilityIndex = draft.facilities?.findIndex((f) => f.facilityId === facilityId) ?? -1;
    if (facilityIndex === -1) return;

    const facilityDetails = draft.facilities[facilityIndex].facilityDetails;
    if (!isCCA3Scheme(facilityDetails.participatingSchemeVersions)) {
      draft.facilities[facilityIndex].cca3BaselineAndTargets = null;
    }
  });
}

import { createDescendingSelector } from '@netz/common/store';
import { UNAVariationReviewRequestTaskPayload, underlyingAgreementQuery } from '@requests/common';

import { activeFacilityExists, canAcceptVariationPayload, canRejectVariationPayload } from '../utils';

const selectCanAccept = createDescendingSelector(underlyingAgreementQuery.selectPayload, canAcceptVariationPayload);

const selectCanReject = createDescendingSelector(underlyingAgreementQuery.selectPayload, canRejectVariationPayload);

const selectRejectionWarning = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAVariationReviewRequestTaskPayload) => !activeFacilityExists(payload),
);

export const underlyingAgreementVariationReviewTaskQuery = {
  selectCanAccept,
  selectCanReject,
  selectRejectionWarning,
};

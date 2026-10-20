import { createDescendingSelector } from '@netz/common/store';
import { UNAVariationReviewRequestTaskPayload, underlyingAgreementQuery } from '@requests/common';

import { canAcceptVariationPayload, canRejectVariationPayload, validFacilityExists } from '../utils';

const selectCanAccept = createDescendingSelector(underlyingAgreementQuery.selectPayload, canAcceptVariationPayload);

const selectCanReject = createDescendingSelector(underlyingAgreementQuery.selectPayload, canRejectVariationPayload);

const selectRejectionWarning = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAVariationReviewRequestTaskPayload) => !validFacilityExists(payload),
);

export const underlyingAgreementVariationReviewTaskQuery = {
  selectCanAccept,
  selectCanReject,
  selectRejectionWarning,
};

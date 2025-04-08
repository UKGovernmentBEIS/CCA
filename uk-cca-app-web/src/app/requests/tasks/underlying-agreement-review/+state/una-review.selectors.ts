import { createDescendingSelector } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';

import { canAcceptPayload, canRejectPayload } from '../utils';

const selectCanAccept = createDescendingSelector(underlyingAgreementQuery.selectPayload, canAcceptPayload);

const selectCanReject = createDescendingSelector(underlyingAgreementQuery.selectPayload, canRejectPayload);

export const underlyingAgreementReviewTaskQuery = {
  selectCanAccept,
  selectCanReject,
};

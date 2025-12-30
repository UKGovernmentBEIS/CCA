import { createDescendingSelector, RequestTaskState, StateSelector } from '@netz/common/store';

import { VariationDetermination } from 'cca-api';

import { UNAVariationReviewRequestTaskPayload } from '../types';
import { underlyingAgreementQuery } from './underlying-agreement.selectors';

const selectPayload: StateSelector<RequestTaskState, UNAVariationReviewRequestTaskPayload> = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload) => payload as UNAVariationReviewRequestTaskPayload,
);

const selectDetermination: StateSelector<RequestTaskState, VariationDetermination> = createDescendingSelector(
  selectPayload,
  (payload) => payload?.determination ?? { type: null },
);

export const underlyingAgreementVariationReviewQuery = {
  selectDetermination,
};

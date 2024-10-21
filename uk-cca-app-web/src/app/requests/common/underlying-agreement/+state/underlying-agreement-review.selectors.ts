import { createDescendingSelector, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { TaskItemStatus } from '../../task-item-status';
import { UNAReviewRequestTaskPayload, UNAVariationReviewRequestTaskPayload } from '../underlying-agreement.types';
import { underlyingAgreementQuery } from './underlying-agreement.selectors';

const selectSectorAssociationId = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
    payload.accountReferenceData.targetUnitAccountDetails.sectorAssociationId,
);

const selectReviewAttachments: StateSelector<RequestTaskState, { [key: string]: string }> = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => payload.reviewAttachments,
);

const selectReviewSectionsCompleted: StateSelector<RequestTaskState, { [key: string]: string }> =
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => payload.reviewSectionsCompleted,
  );

const selectReviewSectionCompleted = (section: string) =>
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
      payload.reviewSectionsCompleted[section],
  );

const selectReviewSectionIsCompleted = (section: string) =>
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
      payload.reviewSectionsCompleted[section] && payload.reviewSectionsCompleted[section] !== TaskItemStatus.UNDECIDED,
  );

const selectSubtaskDecision = (
  group:
    | UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group']
    | UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
) =>
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
      payload.reviewGroupDecisions[group],
  );

const selectFacilitySubtaskDecision = (facility: string) =>
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
      payload.facilitiesReviewGroupDecisions[facility],
  );

const selectDetermination = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => payload.determination,
);

const selectIsCompleted = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => !!payload.determination,
);

export const underlyingAgreementReviewQuery = {
  selectSectorAssociationId,
  selectReviewAttachments,
  selectReviewSectionsCompleted,
  selectReviewSectionCompleted,
  selectReviewSectionIsCompleted,
  selectSubtaskDecision,
  selectFacilitySubtaskDecision,
  selectDetermination,
  selectIsCompleted,
};

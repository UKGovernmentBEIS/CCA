import { createDescendingSelector, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { TaskItemStatus } from '../../task-item-status';
import { UNAReviewRequestTaskPayload, UNAVariationReviewRequestTaskPayload } from '../underlying-agreement.types';
import { overallDecisionStatus } from '../utils';
import { underlyingAgreementQuery } from './underlying-agreement.selectors';

const selectSectorAssociationId = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
    payload.accountReferenceData.targetUnitAccountDetails.sectorAssociationId,
);

const selectReviewAttachments: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => payload.reviewAttachments,
);

const selectReviewSectionsCompleted: StateSelector<RequestTaskState, Record<string, string>> = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) => payload.reviewSectionsCompleted,
);

const selectReviewSectionCompleted = (section: string) =>
  createDescendingSelector(
    underlyingAgreementQuery.selectPayload,
    (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
      payload.reviewSectionsCompleted[section] as TaskItemStatus,
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

const selectDeterminationSubmitted = createDescendingSelector(underlyingAgreementQuery.selectPayload, (payload) =>
  [TaskItemStatus.APPROVED, TaskItemStatus.REJECTED].includes(overallDecisionStatus(payload)),
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
  selectDeterminationSubmitted,
};

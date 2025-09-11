import { createDescendingSelector, RequestTaskState, StateSelector } from '@netz/common/store';

import {
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { TaskItemStatus } from '../../task-item-status';
import { overallDecisionStatus } from '../../utils';
import { FacilityItemViewModel, UNAReviewRequestTaskPayload, UNAVariationReviewRequestTaskPayload } from '../types';
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

const selectFacilitiesItems: StateSelector<RequestTaskState, FacilityItemViewModel[]> = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload) =>
    payload.underlyingAgreement?.facilities?.map((f) => ({
      name: f.facilityDetails.name,
      facilityId: f.facilityId,
      status: f.status,
      workflowStatus: (payload.reviewSectionsCompleted?.[f.facilityId] as TaskItemStatus) ?? TaskItemStatus.UNDECIDED,
    })) ?? [],
);

const selectDetermination = createDescendingSelector(
  underlyingAgreementQuery.selectPayload,
  (payload: UNAReviewRequestTaskPayload | UNAVariationReviewRequestTaskPayload) =>
    payload.determination || { type: null },
);

const selectDeterminationSubmitted = createDescendingSelector(underlyingAgreementQuery.selectPayload, (payload) =>
  [TaskItemStatus.ACCEPTED, TaskItemStatus.REJECTED].includes(overallDecisionStatus(payload)),
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
  selectFacilitiesItems,
};

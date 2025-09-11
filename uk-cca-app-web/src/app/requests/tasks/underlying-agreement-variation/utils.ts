import { RequestTaskStore } from '@netz/common/store';
import {
  SUBTASK_TO_DECISION_MAP,
  TaskItemStatus,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

import { UnderlyingAgreementReviewDecision, UnderlyingAgreementVariationFacilityReviewDecision } from 'cca-api';

export type ReviewProps = {
  reviewSectionsCompleted: Record<string, string>;
  reviewGroupDecisions: Record<string, UnderlyingAgreementReviewDecision>;
};

export type FacilityReviewProps = {
  reviewSectionsCompleted: Record<string, string>;
  facilitiesReviewGroupDecisions: Record<string, UnderlyingAgreementVariationFacilityReviewDecision>;
};

export function resetReviewSection(reviewProps: ReviewProps, subtask: string): ReviewProps {
  return produce(reviewProps, (draft) => {
    const sectionKey = SUBTASK_TO_DECISION_MAP[subtask];
    if (!sectionKey) throw new Error(`Submit Variation Action - Invalid subtask decision key: ${subtask}`);

    draft.reviewSectionsCompleted[subtask] = TaskItemStatus.UNDECIDED;
    delete draft.reviewGroupDecisions[sectionKey];
  });
}

export function resetFacilityReviewSection(reviewProps: FacilityReviewProps, facilityId: string): FacilityReviewProps {
  return produce(reviewProps, (draft) => {
    draft.reviewSectionsCompleted[facilityId] = TaskItemStatus.UNDECIDED;
    delete draft.facilitiesReviewGroupDecisions[facilityId];
  });
}

export function removeFacilityReviewSection(reviewProps: FacilityReviewProps, facilityId: string): FacilityReviewProps {
  return produce(reviewProps, (draft) => {
    delete draft.facilitiesReviewGroupDecisions[facilityId];
    delete draft.reviewSectionsCompleted[facilityId];
  });
}

export function setVariationDetailsReviewSection(reviewProps: ReviewProps): ReviewProps {
  return produce(reviewProps, (draft) => {
    const key = SUBTASK_TO_DECISION_MAP[VARIATION_DETAILS_SUBTASK];
    draft.reviewSectionsCompleted[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.ACCEPTED;
    draft.reviewGroupDecisions[key] = {
      type: 'ACCEPTED',
    };
  });
}

export function extractReviewProps(store: RequestTaskStore): FacilityReviewProps & ReviewProps {
  const reviewSectionsCompleted = store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)() || {};

  const reviewGroupDecisions = store.select(underlyingAgreementVariationQuery.selectReviewGroupDecisions)() || {};

  const facilitiesReviewGroupDecisions =
    store.select(underlyingAgreementVariationQuery.selectFacilityReviewGroupDecisions)() || {};

  return {
    reviewSectionsCompleted,
    reviewGroupDecisions,
    facilitiesReviewGroupDecisions,
  };
}

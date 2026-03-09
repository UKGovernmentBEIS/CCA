import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  OVERALL_DECISION_SUBTASK,
  OverallDecisionWizardStep,
  TaskItemStatus,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationReviewQuery,
} from '@requests/common';

import { reviewSectionsCompleted } from '../../utils';

export const canActivateVariationOverallDecision: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const payload = store.select(requestTaskQuery.selectRequestTaskPayload)();

  // if the review sections are not completed, cannot proceed to overall decision
  if (!reviewSectionsCompleted(payload)) return false;

  const status = store.select(underlyingAgreementReviewQuery.selectReviewSectionCompleted(OVERALL_DECISION_SUBTASK))();
  const statusPending = !status || status === TaskItemStatus.UNDECIDED || status === TaskItemStatus.IN_PROGRESS;

  if (!statusPending) return createUrlTreeFromSnapshot(route, ['summary']);

  const determination = store.select(underlyingAgreementVariationReviewQuery.selectDetermination)();
  if (!determination?.type) return createUrlTreeFromSnapshot(route, [OverallDecisionWizardStep.AVAILABLE_ACTIONS]);

  if (determination.type === 'REJECTED' && !determination.reason) {
    return createUrlTreeFromSnapshot(route, [OverallDecisionWizardStep.EXPLANATION]);
  }

  if (determination.type === 'ACCEPTED' && typeof determination.variationImpactsAgreement !== 'boolean') {
    return createUrlTreeFromSnapshot(route, [OverallDecisionWizardStep.ADDITIONAL_INFO]);
  }

  return createUrlTreeFromSnapshot(route, ['check-your-answers']);
};

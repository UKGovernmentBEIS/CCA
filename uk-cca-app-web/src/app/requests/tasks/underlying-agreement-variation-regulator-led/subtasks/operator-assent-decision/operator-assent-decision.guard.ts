import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import {
  OPERATOR_ASSENT_DECISION_SUBTASK,
  OverallDecisionWizardStep,
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';

export const canActivateOperatorAssentDecision: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);
  const sectionsCompleted = requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionCompleted = sectionsCompleted[OPERATOR_ASSENT_DECISION_SUBTASK] === TaskItemStatus.COMPLETED;
  const determination = requestTaskStore.select(underlyingAgreementVariationRegulatorLedQuery.selectDetermination)();

  if (!determination) return createUrlTreeFromSnapshot(route, [OverallDecisionWizardStep.ADDITIONAL_INFO]);

  if (determination.variationImpactsAgreement !== null) {
    if (!sectionCompleted) {
      return createUrlTreeFromSnapshot(route, ['check-your-answers']);
    } else {
      return createUrlTreeFromSnapshot(route, ['summary']);
    }
  }
};

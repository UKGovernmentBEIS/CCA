import { inject } from '@angular/core';

import { RequestTaskStore } from '@netz/common/store';
import { OverallDecisionWizardStep, underlyingAgreementReviewQuery } from '@requests/common';

export const additionalInfoBacklinkResolver = () => {
  const store = inject(RequestTaskStore);
  return store.select(underlyingAgreementReviewQuery.selectDetermination)()?.type === 'ACCEPTED'
    ? `../${OverallDecisionWizardStep.AVAILABLE_ACTIONS}`
    : `../${OverallDecisionWizardStep.EXPLANATION}`;
};

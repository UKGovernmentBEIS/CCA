import { inject } from '@angular/core';

import { getItemActionHeader } from '@netz/common/pipes';
import { RequestActionPageContentFactoryMap } from '@netz/common/request-action';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';

import {
  UnderlyingAgreementSubmittedRequestActionPayload,
  UnderlyingAgreementVariationSubmittedRequestActionPayload,
} from 'cca-api';

import { AdminTerminationFinalDecisionSubmittedTimelineComponent } from './admin-termination-final-decision-submitted/admin-termination-final-decision-submitted-timeline.component';
import { AdminTerminationSubmittedTimelineComponent } from './admin-termination-submitted/admin-termination-submitted-timeline.component';
import { AdminTerminationWithdrawSubmittedTimelineComponent } from './admin-termination-withdraw-submitted/admin-termination-withdraw-submitted-timeline.component';
import { TargetUnitAccountSubmittedTimelineComponent } from './target-unit-account-creation/target-unit-account-submitted-timeline.component';
import { UnderlyingAgreementActivatedComponent } from './underlying-agreement-activated/underlying-agreement-activated.component';
import { UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-accepted-decision-details/underlying-agreement-reviewed-accepted-decision-details.component';
import { UnderlyingAgreementReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-rejected-decision/underlying-agreement-reviewed-rejected-decision-details.component';
import { getAllUnderlyingAgreementSections } from './underlying-agreement-submitted/underlying-agreement-submitted-task-content';
import { getAllUnderlyingAgreementVariationSections } from './underlying-agreement-variation-submitted/underlying-agreement-variation-submitted-task-content';

export const timelineContent: RequestActionPageContentFactoryMap = {
  TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: TargetUnitAccountSubmittedTimelineComponent,
    };
  },

  UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    const payload = store.select(requestActionQuery.selectActionPayload)();

    return {
      header: getItemActionHeader(action),
      sections: getAllUnderlyingAgreementSections(payload as UnderlyingAgreementSubmittedRequestActionPayload),
    };
  },

  UNDERLYING_AGREEMENT_APPLICATION_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementReviewedRejectedDecisionDetailsComponent,
    };
  },

  UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent,
    };
  },

  UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementActivatedComponent,
    };
  },

  ADMIN_TERMINATION_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: AdminTerminationSubmittedTimelineComponent,
    };
  },

  ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: AdminTerminationWithdrawSubmittedTimelineComponent,
    };
  },

  ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: AdminTerminationFinalDecisionSubmittedTimelineComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    const payload = store.select(requestActionQuery.selectActionPayload)();

    return {
      header: getItemActionHeader(action),
      sections: getAllUnderlyingAgreementVariationSections(
        payload as UnderlyingAgreementVariationSubmittedRequestActionPayload,
      ),
    };
  },
};

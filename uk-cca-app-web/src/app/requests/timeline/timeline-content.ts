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
import { PerformanceDataUploadSubmittedComponent } from './performance-data-upload-submitted/performance-data-upload-submitted.component';
import { SectorMoaGeneratedComponent } from './sector-moa-generated/sector-moa-generated.component';
import { SubsistenceFeesRunCompletedComponent } from './subsistence-fees-run-completed/subsistence-fees-run-completed.component';
import { TargetUnitAccountSubmittedTimelineComponent } from './target-unit-account-creation/target-unit-account-submitted-timeline.component';
import { TuMoaGeneratedComponent } from './tu-moa-generated/tu-moa-generated.component';
import { UnderlyingAgreementActivatedComponent } from './underlying-agreement-activated/underlying-agreement-activated.component';
import { UnderlyingAgreementMigratedComponent } from './underlying-agreement-migrated/underlying-agreement-migrated.component';
import { UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-accepted-decision-details/underlying-agreement-reviewed-accepted-decision-details.component';
import { UnderlyingAgreementReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-rejected-decision/underlying-agreement-reviewed-rejected-decision-details.component';
import { getAllUnderlyingAgreementSections } from './underlying-agreement-submitted/underlying-agreement-submitted-task-content';
import { UnderlyingAgreementVariationActivatedComponent } from './underlying-agreement-variation-activated/underlying-agreement-variation-activated.component';
import { UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed/underlying-agreement-variation-reviewed-accepted-decision-details/underlying-agreement-variation-reviewed-accepted-decision-details.component';
import { UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-variation-reviewed/underlying-agreement-variation-reviewed-rejected-decision/underlying-agreement-variation-reviewed-rejected-decision-details.component';
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
      sections: getAllUnderlyingAgreementSections(
        (payload as UnderlyingAgreementSubmittedRequestActionPayload)?.underlyingAgreement?.facilities,
        'underlying-agreement-submitted/',
      ),
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

  UNDERLYING_AGREEMENT_APPLICATION_MIGRATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementMigratedComponent,
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

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementVariationActivatedComponent,
    };
  },

  PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PerformanceDataUploadSubmittedComponent,
    };
  },

  SUBSISTENCE_FEES_RUN_COMPLETED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: SubsistenceFeesRunCompletedComponent,
    };
  },

  SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: SubsistenceFeesRunCompletedComponent,
    };
  },

  SECTOR_MOA_GENERATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: SectorMoaGeneratedComponent,
    };
  },

  TARGET_UNIT_MOA_GENERATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: TuMoaGeneratedComponent,
    };
  },
};

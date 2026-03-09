import { inject } from '@angular/core';

import { getItemActionHeader } from '@netz/common/pipes';
import { RequestActionPageContentFactoryMap } from '@netz/common/request-action';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { PeerReviewSubmittedComponent, UNAVariationRequestTaskPayload } from '@requests/common';

import { UnderlyingAgreementSubmittedRequestActionPayload } from 'cca-api';

import { AdminTerminationFinalDecisionSubmittedTimelineComponent } from './admin-termination-final-decision-submitted/admin-termination-final-decision-submitted-timeline.component';
import { AdminTerminationSubmittedTimelineComponent } from './admin-termination-submitted/admin-termination-submitted-timeline.component';
import { AdminTerminationWithdrawSubmittedTimelineComponent } from './admin-termination-withdraw-submitted/admin-termination-withdraw-submitted-timeline.component';
import { BuyOutFeeCalculatedComponent } from './buy-out-fee-calculated/buy-out-fee-calculated.component';
import { BuyOutSurplusBatchRunCompletedComponent } from './buy-out-surplus-batch-run-completed/buy-out-surplus-batch-run-completed.component';
import { CcaTerminationProcessingSubmittedComponent } from './cca-termination-processing-submitted/cca-termination-processing-submitted.component';
import { Cca2ExtensionComponent } from './cca2-extension/cca2-extension.component';
import { Cca3MigrationActivatedComponent } from './cca3-migration/activated/cca3-migration-activated.component';
import { Cca3MigrationCompletedComponent } from './cca3-migration/completed/cca3-migration-completed.component';
import { DetailsCorrectiveActionsCompletedComponent } from './facility-audit/details-corrective-actions-completed/details-corrective-actions-completed.component';
import { PreAuditReviewCompletedComponent } from './facility-audit/pre-audit-review-completed/pre-audit-review-completed.component';
import { TrackCorrectiveActionsCompletedComponent } from './facility-audit/track-corrective-actions-completed/track-corrective-actions-completed.component';
import { PATUploadSubmittedComponent } from './performance-account-template-upload-submitted/pat-upload-submitted.component';
import { PerformanceDataUploadSubmittedComponent } from './performance-data-upload-submitted/performance-data-upload-submitted.component';
import { SectorMoaGeneratedComponent } from './sector-moa-generated/sector-moa-generated.component';
import { SubsistenceFeesRunCompletedComponent } from './subsistence-fees-run-completed/subsistence-fees-run-completed.component';
import { SurplusCalculatedComponent } from './surplus-calculated/surplus-calculated.component';
import { TargetUnitAccountSubmittedTimelineComponent } from './target-unit-account-creation/target-unit-account-submitted-timeline.component';
import { TuMoaGeneratedComponent } from './tu-moa-generated/tu-moa-generated.component';
import { UnderlyingAgreementActivatedComponent } from './underlying-agreement-activated/underlying-agreement-activated.component';
import { UnderlyingAgreementMigratedComponent } from './underlying-agreement-migrated/underlying-agreement-migrated.component';
import { UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-accepted-decision-details/underlying-agreement-reviewed-accepted-decision-details.component';
import { UnderlyingAgreementReviewedRejectedDecisionDetailsComponent } from './underlying-agreement-reviewed/underlying-agreement-reviewed-rejected-decision/underlying-agreement-reviewed-rejected-decision-details.component';
import { getAllUnderlyingAgreementSections } from './underlying-agreement-submitted/underlying-agreement-submitted-task-content';
import { UnderlyingAgreementVariationActivatedComponent } from './underlying-agreement-variation-activated/underlying-agreement-variation-activated.component';
import { UnderlyingAgreementVariationCompletedComponent } from './underlying-agreement-variation-completed/underlying-agreement-variation-completed.component';
import { UnARegulatorLedVariationReviewedDecisionDetailsComponent } from './underlying-agreement-variation-regulator-led-reviewed/peer-review-decision.component';
import { UnARegulatorLedVariationSubmittedComponent } from './underlying-agreement-variation-regulator-led-submitted/underlying-agreement-variation-regulator-led-submitted.component';
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
        (payload as UnderlyingAgreementSubmittedRequestActionPayload)?.underlyingAgreement,
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

  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
    };
  },

  UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
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

  ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
    };
  },

  ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();
    const payload = store.select(requestActionQuery.selectActionPayload)();

    return {
      header: getItemActionHeader(action),
      sections: getAllUnderlyingAgreementVariationSections(
        (payload as UNAVariationRequestTaskPayload)?.underlyingAgreement,
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

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_COMPLETED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementVariationCompletedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PeerReviewSubmittedComponent,
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

  PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PATUploadSubmittedComponent,
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

  BUY_OUT_SURPLUS_RUN_COMPLETED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: BuyOutSurplusBatchRunCompletedComponent,
    };
  },

  BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: BuyOutSurplusBatchRunCompletedComponent,
    };
  },

  TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: BuyOutFeeCalculatedComponent,
    };
  },

  TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: SurplusCalculatedComponent,
    };
  },

  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: Cca3MigrationCompletedComponent,
    };
  },

  CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: Cca3MigrationActivatedComponent,
    };
  },

  CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: Cca2ExtensionComponent,
    };
  },

  FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: PreAuditReviewCompletedComponent,
    };
  },

  FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: DetailsCorrectiveActionsCompletedComponent,
    };
  },

  FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: TrackCorrectiveActionsCompletedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnARegulatorLedVariationSubmittedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_COMPLETED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnARegulatorLedVariationSubmittedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_ACTIVATED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnderlyingAgreementVariationActivatedComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_ACCEPTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnARegulatorLedVariationReviewedDecisionDetailsComponent,
    };
  },

  UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_REJECTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: UnARegulatorLedVariationReviewedDecisionDetailsComponent,
    };
  },

  CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED: () => {
    const store = inject(RequestActionStore);
    const action = store.select(requestActionQuery.selectAction)();

    return {
      header: getItemActionHeader(action),
      component: CcaTerminationProcessingSubmittedComponent,
    };
  },
};

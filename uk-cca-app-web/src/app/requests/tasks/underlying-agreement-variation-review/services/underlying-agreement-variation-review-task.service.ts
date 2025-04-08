import { Injectable } from '@angular/core';

import { tap } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import {
  DecisionFormValue,
  DecisionWithDateFormValue,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';

import {
  CcaDecisionNotification,
  Determination,
  Facility,
  UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { createProposedUnderlyingAgreementVariationPayload } from '../utils';
import { UnderlyingAgreementVariationReviewApiService } from './underlying-agreement-variation-review-api.service';

@Injectable()
export class UnderlyingAgreementVariationReviewTaskService extends TaskService {
  get payload(): UNAVariationReviewRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)() as UNAVariationReviewRequestTaskPayload;
  }

  set payload(payload: UNAVariationReviewRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  saveDecision(
    decision: DecisionFormValue,
    group: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    subtask: string,
  ) {
    const payload: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload = {
      decision: { type: decision.type, details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) } },
      group,
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [subtask]: TaskItemStatus.UNDECIDED,
        [OVERALL_DECISION_SUBTASK]: TaskItemStatus.UNDECIDED,
      },
      determination: { ...this.payload.determination, type: null, reason: null },
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION')
      .pipe(
        tap((payload) => {
          this.payload = payload;
        }),
      );
  }

  saveFacilityDecision(decision: DecisionWithDateFormValue, facility: Facility) {
    const changeStartDate = decision?.changeDate?.[0];

    const payload: UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload = {
      decision: {
        type: decision.type,
        changeStartDate: decision.type === 'ACCEPTED' && facility.status === 'NEW' ? !!changeStartDate : null,
        startDate: decision.startDate as any, // bypass incorrect api type. Should be date, it is string
        details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) },
        facilityStatus: facility.status,
      },
      group: facility.facilityId,
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [facility.facilityId]: TaskItemStatus.UNDECIDED,
        [OVERALL_DECISION_SUBTASK]: TaskItemStatus.UNDECIDED,
      },
      determination: { ...this.payload.determination, type: null },
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION')
      .pipe(
        tap((payload) => {
          this.payload = payload;
        }),
      );
  }

  saveReviewDetermination(determination: Partial<Determination>) {
    const payload: UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination: {
        ...this.payload.determination,
        ...determination,
        ...(determination?.type === 'ACCEPTED' ? { reason: null } : {}),
      },
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [OVERALL_DECISION_SUBTASK]: TaskItemStatus.UNDECIDED,
      },
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService).saveDetermination(payload).pipe(
      tap((payload) => {
        this.payload = payload;
      }),
    );
  }

  submitReviewDetermination(determination: Partial<Determination>) {
    const taskItemStatus = determination.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;

    const payload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [OVERALL_DECISION_SUBTASK]: taskItemStatus,
      },
      determination: {
        ...this.payload.determination,
        ...determination,
      },
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService).saveDetermination(payload).pipe(
      tap((payload) => {
        this.payload = payload;
      }),
    );
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementVariationReviewApiService).notifyOperator(
      notificationPayload,
      createProposedUnderlyingAgreementVariationPayload(this.payload),
    );
  }
}

import { Injectable } from '@angular/core';

import { tap } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import {
  DecisionFormValue,
  DecisionWithDateFormValue,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';

import {
  CcaDecisionNotification,
  Determination,
  UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { createProposedUnderlyingAgreementPayload } from '../utils';
import { UnderlyingAgreementReviewTaskApiService } from './underlying-agreement-review-api.service';

@Injectable()
export class UnderlyingAgreementReviewTaskService extends TaskService {
  get payload(): UNAReviewRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)();
  }

  set payload(payload: UNAReviewRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  saveDecision(
    decision: DecisionFormValue,
    group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    subtask: string,
  ) {
    const payload: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload = {
      decision: { type: decision.type, details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) } },
      group,
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [subtask]: TaskItemStatus.UNDECIDED,
        [OVERALL_DECISION_SUBTASK]: TaskItemStatus.UNDECIDED,
      },
      determination: { ...this.payload.determination, type: null, reason: null },
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementReviewTaskApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION')
      .pipe(
        tap((payload) => {
          this.payload = payload;
        }),
      );
  }

  saveFacilityDecision(decision: DecisionWithDateFormValue, facilityId: string) {
    const changeStartDate = decision?.changeDate?.[0];

    const payload: UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload = {
      decision: {
        type: decision.type,
        changeStartDate: decision.type === 'ACCEPTED' ? !!changeStartDate : null,
        startDate: decision.startDate as any, // bypass incorrect api type. Should be date, it is string
        details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) },
      },
      group: facilityId,
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [facilityId]: TaskItemStatus.UNDECIDED,
        [OVERALL_DECISION_SUBTASK]: TaskItemStatus.UNDECIDED,
      },
      determination: { ...this.payload.determination, type: null, reason: null },
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementReviewTaskApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION')
      .pipe(
        tap((payload) => {
          this.payload = payload;
        }),
      );
  }

  saveReviewDetermination(determination: Partial<Determination>) {
    const payload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD',
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

    return (this.apiService as UnderlyingAgreementReviewTaskApiService).saveDetermination(payload).pipe(
      tap((payload) => {
        this.payload = payload;
      }),
    );
  }

  submitReviewDetermination(determination: Partial<Determination>) {
    const taskItemStatus = determination.type === 'ACCEPTED' ? TaskItemStatus.APPROVED : TaskItemStatus.REJECTED;

    const payload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      reviewSectionsCompleted: {
        ...this.payload.reviewSectionsCompleted,
        [OVERALL_DECISION_SUBTASK]: taskItemStatus,
      },
      determination: {
        ...this.payload.determination,
        ...determination,
      },
    };

    return (this.apiService as UnderlyingAgreementReviewTaskApiService).saveDetermination(payload).pipe(
      tap((payload) => {
        this.payload = payload;
      }),
    );
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementReviewTaskApiService).notifyOperator(
      notificationPayload,
      createProposedUnderlyingAgreementPayload(this.payload),
    );
  }
}

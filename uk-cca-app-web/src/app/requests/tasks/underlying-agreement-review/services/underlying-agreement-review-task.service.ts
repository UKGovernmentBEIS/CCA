import { Injectable } from '@angular/core';

import { tap } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  DecisionFormValue,
  DecisionWithDateFormValue,
  TaskItemStatus,
  UNAReviewRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';
import { UuidFilePair } from '@shared/components';
import produce from 'immer';

import {
  CcaDecisionNotification,
  Determination,
  UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { UnderlyingAgreementReviewTaskApiService } from './underlying-agreement-review-api.service';

@Injectable()
export class UnderlyingAgreementReviewTaskService extends TaskService {
  get payload(): UNAReviewRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)();
  }

  set payload(payload: UNAReviewRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  private updateDecision(
    decision: DecisionFormValue,
    group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    subtask: string,
  ) {
    this.payload = produce(this.payload, (p) => {
      p.reviewGroupDecisions[group] = {
        type: decision.type,
        details: {
          notes: decision.notes,
          files: decision.files.map((f) => f.uuid),
        },
      };
      decision.files.forEach((f) => {
        p.reviewAttachments[f.uuid] = f.file.name;
      });
      p.reviewSectionsCompleted[subtask] = TaskItemStatus.UNDECIDED;
    });
  }

  private updateFacilityDecision(decision: DecisionWithDateFormValue, facilityId: string) {
    const pipe = new GovukDatePipe();

    this.payload = produce(this.payload, (p) => {
      p.facilitiesReviewGroupDecisions[facilityId] = {
        type: decision.type,
        changeStartDate: decision?.changeDate?.[0],
        startDate: pipe.transform(decision.startDate) || null,
        details: {
          notes: decision.notes,
          files: decision.files.map((f) => f.uuid),
        },
      };
      decision.files.forEach((f) => {
        p.reviewAttachments[f.uuid] = f.file.name;
      });
      p.reviewSectionsCompleted[facilityId] = TaskItemStatus.UNDECIDED;
    });
  }

  updateDetermination(determination: Partial<Determination>, files: UuidFilePair[] = []) {
    this.payload = produce(this.payload, (p) => {
      p.determination = { ...p.determination, ...determination };
      if (determination?.type === 'ACCEPTED') delete p.determination.reason;
      files.forEach((f) => (p.reviewAttachments[f.uuid] = f.file.name));
    });
  }

  saveDecision(
    decision: DecisionFormValue,
    group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    subtask: string,
  ) {
    const payload: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload = {
      decision: { type: decision.type, details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) } },
      group,
      reviewSectionsCompleted: { ...this.payload.reviewSectionsCompleted, [subtask]: TaskItemStatus.UNDECIDED },
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
    };
    return (this.apiService as UnderlyingAgreementReviewTaskApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION')
      .pipe(
        tap(() => {
          this.updateDecision(decision, group, subtask);
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
      reviewSectionsCompleted: { ...this.payload.reviewSectionsCompleted, [facilityId]: TaskItemStatus.UNDECIDED },
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
    };
    return (this.apiService as UnderlyingAgreementReviewTaskApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION')
      .pipe(
        tap(() => {
          this.updateFacilityDecision(decision, facilityId);
        }),
      );
  }
  saveReviewDetermination(determination: Determination) {
    const payload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination,
    };
    return (this.apiService as UnderlyingAgreementReviewTaskApiService).saveDetermination(payload).pipe(
      tap(() => {
        this.updateDetermination(determination);
      }),
    );
  }
  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementReviewTaskApiService).notifyOperator(notificationPayload);
  }
}

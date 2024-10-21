import { Injectable } from '@angular/core';

import { tap } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  DecisionFormValue,
  DecisionWithDateFormValue,
  TaskItemStatus,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
} from '@requests/common';
import { UuidFilePair } from '@shared/components';
import produce from 'immer';

import {
  CcaDecisionNotification,
  Determination,
  Facility,
  UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload,
} from 'cca-api';

import { UnderlyingAgreementVariationReviewApiService } from './underlying-agreement-variation-review-api.service';

@Injectable()
export class UnderlyingAgreementVariationReviewTaskService extends TaskService {
  get payload(): UNAVariationReviewRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)() as UNAVariationReviewRequestTaskPayload;
  }

  set payload(payload: UNAVariationReviewRequestTaskPayload) {
    this.store.setPayload(payload);
  }

  private updateDecision(
    decision: DecisionFormValue,
    group: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
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

  private updateFacilityDecision(decision: DecisionWithDateFormValue, facility: Facility) {
    const pipe = new GovukDatePipe();

    this.payload = produce(this.payload, (p) => {
      p.facilitiesReviewGroupDecisions[facility.facilityId] = {
        type: decision.type,
        changeStartDate: decision?.changeDate?.[0] ?? null,
        startDate: pipe.transform(decision.startDate) || null,
        details: {
          notes: decision.notes,
          files: decision.files.map((f) => f.uuid),
        },
        facilityStatus: facility.status,
      };
      decision.files.forEach((f) => {
        p.reviewAttachments[f.uuid] = f.file.name;
      });
      p.reviewSectionsCompleted[facility.facilityId] = TaskItemStatus.UNDECIDED;
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
    group: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    subtask: string,
  ) {
    const payload: UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload = {
      decision: { type: decision.type, details: { notes: decision.notes, files: decision.files.map((f) => f.uuid) } },
      group,
      reviewSectionsCompleted: { ...this.payload.reviewSectionsCompleted, [subtask]: TaskItemStatus.UNDECIDED },
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_GROUP_DECISION')
      .pipe(
        tap(() => {
          this.updateDecision(decision, group, subtask);
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
      },
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService)
      .saveReviewDecision(payload, 'UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION')
      .pipe(
        tap(() => {
          this.updateFacilityDecision(decision, facility);
        }),
      );
  }

  saveReviewDetermination(determination: Determination) {
    const payload: UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload = {
      payloadType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination,
    };

    return (this.apiService as UnderlyingAgreementVariationReviewApiService).saveDetermination(payload).pipe(
      tap(() => {
        this.updateDetermination(determination);
      }),
    );
  }

  notifyOperator(notificationPayload: CcaDecisionNotification) {
    return (this.apiService as UnderlyingAgreementVariationReviewApiService).notifyOperator(notificationPayload);
  }
}

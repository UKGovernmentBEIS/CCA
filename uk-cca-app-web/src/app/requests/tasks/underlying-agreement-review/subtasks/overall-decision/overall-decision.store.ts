import { inject, Injectable } from '@angular/core';

import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { UNAReviewRequestTaskPayload } from '@requests/common';
import { UuidFilePair } from '@shared/components';
import { transformFilesToAttachments } from '@shared/utils';
import produce from 'immer';

import { Determination } from 'cca-api';

@Injectable()
export class OverallDecisionStore {
  determination: Determination | null = null;
  private store = inject(RequestTaskStore);
  private taskService = inject(TaskService);
  updateDetermination(payload: Partial<Determination>, files: UuidFilePair[] = []) {
    this.determination = { ...this.determination, ...payload };
    if (files.length) {
      this.updateFiles(transformFilesToAttachments(files));
    }
  }
  reset() {
    this.determination = null;
  }
  private updateFiles(files: Record<string, string>): void {
    const payload: UNAReviewRequestTaskPayload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
    this.taskService.payload = produce(payload, (p) => {
      p.reviewAttachments = { ...p.reviewAttachments, ...files };
    });
  }
}

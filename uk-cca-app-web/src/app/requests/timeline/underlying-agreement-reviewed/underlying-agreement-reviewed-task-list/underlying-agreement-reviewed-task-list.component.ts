import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent, TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  UnderlyingAgreementDecisionRequestActionPayload,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';

import { getAllUnderlyingAgreementReviewTimelineSections } from '../underlying-agreement-reviewed-task-content';

@Component({
  selector: 'cca-underlying-agreement-review-task-list',
  standalone: true,
  imports: [TaskListComponent, ReturnToTaskOrActionPageComponent, PageHeadingComponent],
  templateUrl: './underlying-agreement-reviewed-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  sections = computed(() =>
    getAllUnderlyingAgreementReviewTimelineSections(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementDecisionRequestActionPayload,
    ),
  );
}

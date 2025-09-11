import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  UnderlyingAgreementDecisionRequestActionPayload,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';

import { getAllUnderlyingAgreementReviewTimelineSections } from '../underlying-agreement-reviewed-task-content';

@Component({
  selector: 'cca-underlying-agreement-review-task-list',
  template: `
    <netz-page-heading> Underlying agreement application</netz-page-heading>
    <netz-task-list [sections]="sections()" />
  `,
  standalone: true,
  imports: [TaskListComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly sections = computed(() =>
    getAllUnderlyingAgreementReviewTimelineSections(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementDecisionRequestActionPayload,
    ),
  );
}

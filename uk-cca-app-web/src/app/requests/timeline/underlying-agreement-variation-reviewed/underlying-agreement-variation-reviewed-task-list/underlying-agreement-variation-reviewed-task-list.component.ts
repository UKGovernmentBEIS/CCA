import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../+state/underlying-agreement-variation-reviewed-request-action.selectors';
import { getAllUnderlyingAgreementVariationReviewTimelineSections } from '../underlying-agreement-variation-reviewed-task-content';

@Component({
  selector: 'cca-underlying-agreement-variation-review-task-list',
  template: `
    <netz-page-heading>Underlying agreement variation</netz-page-heading>
    <netz-task-list [sections]="sections()" />
  `,
  standalone: true,
  imports: [TaskListComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly sections = computed(() =>
    getAllUnderlyingAgreementVariationReviewTimelineSections(
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectPayload)(),
    ),
  );
}

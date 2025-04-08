import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../+state/underlying-agreement-variation-reviewed-request-action.selectors';
import { getAllUnderlyingAgreementVariationReviewTimelineSections } from '../underlying-agreement-variation-reviewed-task-content';

@Component({
  selector: 'cca-underlying-agreement-variation-review-task-list',
  standalone: true,
  imports: [TaskListComponent, PageHeadingComponent],
  templateUrl: './underlying-agreement-variation-reviewed-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  sections = computed(() =>
    getAllUnderlyingAgreementVariationReviewTimelineSections(
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectPayload)(),
    ),
  );
}

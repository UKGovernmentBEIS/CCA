import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, TaskListComponent } from '@netz/common/components';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';

import { UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload } from 'cca-api';

import { getAllUnARegulatorLedVariationTimelineSections } from '../underlying-agreement-variation-regulator-led-task-content';

@Component({
  selector: 'cca-underlying-agreement-variation-regulator-led-submitted-task-list',
  template: `
    <netz-page-heading>Underlying agreement variation</netz-page-heading>
    <netz-task-list [sections]="sections()" />
  `,
  imports: [TaskListComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnARegulatorLedVariationSubmittedTaskListComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly sections = computed(() =>
    getAllUnARegulatorLedVariationTimelineSections(
      this.requestActionStore.select(
        requestActionQuery.selectActionPayload,
      )() as UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload,
    ),
  );
}

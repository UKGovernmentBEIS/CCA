import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';

import { TaskListComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { tprFormActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { toTPRReportingSubmittedSummary } from './tpr-reporting-form-submitted-summary-data';
import { getAllTPRReportingFormSubmittedSections } from './tpr-reporting-form-submitted-task-content';

@Component({
  selector: 'cca-tpr-reporting-form-submitted',
  template: `
    <div class="govuk-!-width-two-thirds">
      <cca-summary [data]="summaryData()" />
      <netz-task-list [sections]="sections()" />
    </div>
  `,
  imports: [SummaryComponent, TaskListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprReportingFormSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly details = this.requestActionStore.select(tprFormActionQuery.selectDetails);

  protected readonly summaryData = computed(() => toTPRReportingSubmittedSummary(this.details()));
  protected readonly sections = signal(getAllTPRReportingFormSubmittedSections());
}

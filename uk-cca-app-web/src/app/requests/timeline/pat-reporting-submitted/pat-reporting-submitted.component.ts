import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload } from 'cca-api';

import { toPATReportingSummaryData } from './pat-reporting-submitted-summary-data';

@Component({
  selector: 'cca-pat-reporting-submitted',
  template: `
    <div class="govuk-!-width-two-thirds">
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatReportingSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly payload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  ) as Signal<FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload>;

  protected readonly summaryData = computed(() => toPATReportingSummaryData(this.payload()));
}

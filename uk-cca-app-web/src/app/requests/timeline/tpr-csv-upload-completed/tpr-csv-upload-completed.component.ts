import { ChangeDetectionStrategy, Component, computed, inject, Signal } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { PerformanceDataFacilityDataUploadCompletedRequestActionPayload } from 'cca-api';

import { toTPRCSVUploadCompletedSummary } from './tpr-csv-upload-completed-summary-data';

@Component({
  selector: 'cca-tpr-csv-upload-completed',
  template: `
    <div class="govuk-!-width-two-thirds">
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprCSVUploadCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly payload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  ) as Signal<PerformanceDataFacilityDataUploadCompletedRequestActionPayload>;

  protected readonly summaryData = computed(() => toTPRCSVUploadCompletedSummary(this.payload()));
}

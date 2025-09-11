import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { performanceDataUploadSubmittedActionQuery } from './+state/performance-data-upload.state';
import { toPerformanceUploadSubmittedSummaryData } from './performance-data-upload-submitted-summary-data';

@Component({
  selector: 'cca-performance-data-download-submitted',
  template: `<cca-summary [data]="summaryData()" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly payload = this.requestActionStore.select(performanceDataUploadSubmittedActionQuery.selectPayload);
  private readonly creationDate = this.requestActionStore.select(
    performanceDataUploadSubmittedActionQuery.selectCreationDate,
  );

  protected readonly summaryData = computed(() => {
    return toPerformanceUploadSubmittedSummaryData(this.payload(), this.creationDate());
  });
}

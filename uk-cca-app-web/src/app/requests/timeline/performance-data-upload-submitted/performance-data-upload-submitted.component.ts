import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { performanceDataUploadSubmittedActionQuery } from './+state/performance-data-upload.state';
import { toPerformanceUploadSubmittedSummaryData } from './performance-data-upload-submitted-summary-data';

@Component({
  selector: 'cca-performance-data-download-submitted',
  standalone: true,
  imports: [SummaryComponent],
  templateUrl: './performance-data-upload-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  readonly payload = this.requestActionStore.select(performanceDataUploadSubmittedActionQuery.selectPayload);
  readonly creationDate = this.requestActionStore.select(performanceDataUploadSubmittedActionQuery.selectCreationDate);
  readonly summaryData = computed(() => {
    return toPerformanceUploadSubmittedSummaryData(this.payload(), this.creationDate());
  });
}

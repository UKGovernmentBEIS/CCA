import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { patUploadSubmittedActionQuery } from './+state/pat-upload.state';
import { toPatSubmittedSummaryData } from './pat-upload-submitted-summary-data';

@Component({
  selector: 'cca-pat-upload-submitted',
  template: `<cca-summary [data]="summaryData" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PATUploadSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = toPatSubmittedSummaryData(
    this.requestActionStore.select(patUploadSubmittedActionQuery.selectPayload)(),
  );
}

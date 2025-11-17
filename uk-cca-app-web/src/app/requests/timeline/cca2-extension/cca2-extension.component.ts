import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { toCca2ExtensionSummaryData } from './cca2-extension-summary-data';

@Component({
  selector: 'cca-cca2-extension',
  template: ` <cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca2ExtensionComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;

  protected readonly data = toCca2ExtensionSummaryData(
    this.actionPayload?.defaultContacts,
    this.actionPayload?.officialNotice,
    this.actionPayload?.underlyingAgreementDocument,
    './file-download',
  );
}

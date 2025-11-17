import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload } from 'cca-api';

import { toCca3MigrationActivatedSummaryData } from './cca3-migration-activated-summary-data';

@Component({
  selector: 'cca-cca3-migration-activated',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3MigrationActivatedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload;

  protected readonly data = toCca3MigrationActivatedSummaryData(
    this.actionPayload?.activationDetails,
    this.actionPayload?.activationAttachments,
    this.actionPayload?.defaultContacts,
    this.actionPayload?.officialNotice,
    this.actionPayload?.underlyingAgreementDocument,
    this.actionPayload?.usersInfo,
    this.actionPayload?.decisionNotification,
    './file-download',
  );
}

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { MigratedFacilitiesListComponent } from '../migrated-facilities-list/migrated-facilities-list.component';
import { toCca3MigrationCompletedSummaryData } from './cca3-migration-completed-summary-data';

@Component({
  selector: 'cca-cca3-migration-completed',
  template: `
    @if (hasCCA3Facility) {
      <cca-summary [data]="data" />
    }

    <cca-migrated-facilities-list [migratedFacilities]="facilities" />
  `,
  imports: [SummaryComponent, MigratedFacilitiesListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3MigrationCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;

  protected readonly hasCCA3Facility = this.actionPayload?.facilityMigrationDataList?.some(
    (f) => f.participatingInCca3Scheme,
  );

  protected readonly data = toCca3MigrationCompletedSummaryData(
    this.actionPayload?.defaultContacts,
    this.actionPayload?.officialNotice,
    this.actionPayload?.underlyingAgreementDocument,
    './file-download',
  );

  protected readonly facilities = this.actionPayload?.facilityMigrationDataList ?? [];
}

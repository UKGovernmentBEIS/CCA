import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { toMigratedFacilitySummaryData } from '../migrated-facility-summary-data';

@Component({
  selector: 'cca-migrated-facility-details',
  template: `
    <div>
      <netz-page-heading>{{ facility.facilityName }}</netz-page-heading>
      <p class="govuk-caption-m govuk-!-margin-bottom-8">{{ requestAction.creationDate | govukDate: 'datetime' }}</p>
      <cca-summary [data]="summaryData" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent, GovukDatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MigratedFacilityDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload;

  protected readonly requestAction = this.requestActionStore.select(requestActionQuery.selectAction)();

  protected readonly facility = this.actionPayload.facilityMigrationDataList.find(
    (f) => f.facilityBusinessId === this.facilityId,
  );

  protected readonly summaryData = toMigratedFacilitySummaryData(this.facility);
}

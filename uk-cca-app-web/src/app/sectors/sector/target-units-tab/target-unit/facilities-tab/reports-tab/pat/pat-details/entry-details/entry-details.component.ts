import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { SummaryComponent } from '@shared/components';
import { toFacilityPATReportingSummaryData } from '@shared/utils';

import { FacilityPATReportStore } from '../../../../facility-pat-report.store';

@Component({
  selector: 'cca-entry-details',
  template: `
    <netz-page-heading>Entry {{ entryId() + 1 }}</netz-page-heading>
    <cca-summary [data]="summaryData()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
})
export class EntryDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityPATReportStore = inject(FacilityPATReportStore);

  private readonly state = this.facilityPATReportStore.stateAsSignal;

  protected readonly entryId = signal(+this.activatedRoute.snapshot.params.entryId);

  private readonly action = computed(() => this.state().details?.data?.savingActions[this.entryId()]);

  protected readonly summaryData = computed(() => toFacilityPATReportingSummaryData(this.action()));
}

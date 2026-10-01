import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { toFacilityPATReportingSummaryData } from '@shared/utils';

import { FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload } from 'cca-api';

@Component({
  selector: 'cca-facility-pat-reporting-submitted-details',
  template: `
    <netz-page-heading>Entry {{ actionId() + 1 }}</netz-page-heading>
    <cca-summary [data]="summaryData()" />
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityPATReportingSubmittedDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly payload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  ) as Signal<FacilityPerformanceAccountTemplateProcessingSubmittedRequestActionPayload>;

  protected readonly actionId = signal(+this.activatedRoute.snapshot.params.actionId);

  private readonly action = computed(() => this.payload().performanceData.savingActions[this.actionId()]);

  protected readonly summaryData = computed(() => toFacilityPATReportingSummaryData(this.action()));
}

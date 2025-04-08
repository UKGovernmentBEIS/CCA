import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';

import { BreadcrumbService } from '@netz/common/navigation';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { SubsistenceFeesRunCompletedRequestActionPayload } from 'cca-api';

import { toSubsistenceFeesRunCompletedSummaryData } from './subsistence-fees-run-completed-summary-data';

@Component({
  selector: 'cca-subsistence-fees-run-completed',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubsistenceFeesRunCompletedComponent implements OnInit {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly breadcrumbService = inject(BreadcrumbService);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as SubsistenceFeesRunCompletedRequestActionPayload;

  readonly data = toSubsistenceFeesRunCompletedSummaryData(this.actionPayload);

  ngOnInit() {
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Subsistence fees',
        link: ['/', 'subsistence-fees'],
        fragment: 'workflow-history',
      },
      {
        text: this.actionPayload.paymentRequestId,
        link: ['/', 'subsistence-fees', 'workflow-history', this.actionPayload.paymentRequestId],
      },
    ]);
  }
}

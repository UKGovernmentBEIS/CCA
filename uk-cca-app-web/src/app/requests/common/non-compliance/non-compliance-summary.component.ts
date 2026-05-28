import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { nonComplianceDetailsQuery } from './non-compliance-details.selectors';
import { toNonComplianceSummaryData } from './to-non-compliance-summary-data';

@Component({
  selector: 'cca-non-compliance-summary',
  template: `
    <netz-page-heading caption="Non-compliance details">Summary</netz-page-heading>
    <cca-summary [data]="data()" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceSummaryComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly details = this.store.select(nonComplianceDetailsQuery.selectNonComplianceDetails);
  private readonly workflows = this.store.select(nonComplianceDetailsQuery.selectAllRelevantWorkflows);
  private readonly facilities = this.store.select(nonComplianceDetailsQuery.selectAllRelevantFacilities);

  protected readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable);
  protected readonly data = computed(() =>
    toNonComplianceSummaryData(this.details(), this.workflows() ?? {}, this.facilities() ?? {}, this.isEditable()),
  );
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementMigratedQuery } from './+state/underlying-agreement-migrated.selectors';
import { toUnderlyingAgreementMigratedSummaryData } from './underlying-agreement-migrated-summary-data';

@Component({
  selector: 'cca-underlying-agreement-migrated',
  template: `<cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementMigratedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly migratedPayload = computed(() => ({
    underlyingAgreementDocument: this.requestActionStore.select(
      underlyingAgreementMigratedQuery.selectUnderlyingAgreementDocument,
    )(),
    underlyingAgreementAttachments: this.requestActionStore.select(
      underlyingAgreementMigratedQuery.selectUnderlyingAgreementAttachments,
    )(),
  }));

  protected readonly summaryData = computed(() => toUnderlyingAgreementMigratedSummaryData(this.migratedPayload()));
}

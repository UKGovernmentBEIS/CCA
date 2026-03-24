import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toOperatorAssentDecisionSummaryData } from '../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-operator-assent-decision-submitted',
  template: `
    <div>
      <netz-page-heading>Determine operator assent</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAssentDecisionSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toOperatorAssentDecisionSummaryData(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectDetermination)(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectRegulatorAttachments)(),
      '../../file-download',
      false,
    ),
  );
}

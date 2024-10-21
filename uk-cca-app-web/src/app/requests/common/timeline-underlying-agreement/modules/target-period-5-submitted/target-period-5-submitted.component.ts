import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';

import { toBaselineAndTargetsSummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-target-period-5',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './target-period-5-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod5SubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryData(
      true,
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod5Details)().exist,
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAccountReferenceDataSectorAssociationDetails,
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod5Details)().details,
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
    ),
  );
}

import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';

import { toVariationDetailsSummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-variation-details',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './variation-details-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VariationDetailsSubmittedComponent {

  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toVariationDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectVariationDetails,
      )(),
      false,
    ),
  );
}

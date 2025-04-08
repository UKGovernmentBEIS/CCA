import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  toAuthorisationAdditionalEvidenceSummaryDataWithDecision,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-authorisation-additional-evidence',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './authorisation-additional-evidence.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  readonly summaryData = computed(() =>
    toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAuthorisationAndAdditionalEvidence)(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectSubtaskDecision(
          'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
        ),
      )(),
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments)(),
    ),
  );
}

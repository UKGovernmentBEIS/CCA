import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toAuthorisationAdditionalEvidenceSummaryData } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-authorisation-additional-evidence',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './authorisation-additional-evidence-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toAuthorisationAdditionalEvidenceSummaryData(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAuthorisationAndAdditionalEvidence)(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      false,
      '../../file-download',
    ),
  );
}

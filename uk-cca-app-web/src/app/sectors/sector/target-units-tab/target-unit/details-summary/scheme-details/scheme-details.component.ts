import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { DetailsComponent } from '@netz/govuk-components';
import { fileUtils } from '@shared/utils';

import { UnderlyingAgreementDocumentDetailsDTO } from 'cca-api';

import { AgreementDetailsComponent } from './agreement-details/agreement-details.component';

interface AgreementsState {
  bothActive: boolean;
  bothTerminated: boolean;
  cca2Active: boolean;
  cca3Active: boolean;
}

@Component({
  selector: 'cca-scheme-details',
  templateUrl: './scheme-details.component.html',
  imports: [AgreementDetailsComponent, DetailsComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SchemeDetailsComponent {
  protected readonly downloadURL = input.required<string>();
  protected readonly mainColumnClass = input.required<'govuk-grid-column-two-thirds' | 'govuk-grid-column-full'>();
  protected readonly cca2Details = input.required<UnderlyingAgreementDocumentDetailsDTO>();
  protected readonly cca3Details = input.required<UnderlyingAgreementDocumentDetailsDTO>();

  protected readonly cca2FileDocument = computed(() =>
    fileUtils.toDownloadableDocument([this.cca2Details()?.fileDocument], this.downloadURL()),
  );
  protected readonly cca3FileDocument = computed(() =>
    fileUtils.toDownloadableDocument([this.cca3Details()?.fileDocument], this.downloadURL()),
  );

  protected readonly agreementsState = computed<AgreementsState>(() => {
    const cca2Active = this.cca2Details() && !this.cca2Details().terminatedDate;
    const cca3Active = this.cca3Details() && !this.cca3Details().terminatedDate;

    return {
      bothActive: cca2Active && cca3Active,
      bothTerminated: !cca2Active && !cca3Active,
      cca2Active: cca2Active && !cca3Active,
      cca3Active: !cca2Active && cca3Active,
    };
  });
}

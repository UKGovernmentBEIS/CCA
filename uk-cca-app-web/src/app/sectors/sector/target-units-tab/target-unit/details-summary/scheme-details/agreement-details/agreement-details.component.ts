import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { SummaryDownloadFilesComponent } from '@shared/components';
import { DownloadableFile } from '@shared/utils';

import { UnderlyingAgreementDocumentDetailsDTO } from 'cca-api';

@Component({
  selector: 'cca-agreement-details',
  templateUrl: './agreement-details.component.html',
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    SummaryDownloadFilesComponent,
    DatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AgreementDetailsComponent {
  protected readonly agreement = input.required<UnderlyingAgreementDocumentDetailsDTO>();
  protected readonly title = input.required<string>();
  protected readonly files = input.required<DownloadableFile[]>();
  protected readonly mainColumnClass = input.required<'govuk-grid-column-two-thirds' | 'govuk-grid-column-full'>();

  protected readonly isTerminated = computed(() => !!this.agreement()?.terminatedDate);
}

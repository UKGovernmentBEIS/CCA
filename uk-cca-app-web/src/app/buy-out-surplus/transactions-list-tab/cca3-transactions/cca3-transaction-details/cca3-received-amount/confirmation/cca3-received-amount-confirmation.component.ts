import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

@Component({
  selector: 'cca-cca3-received-amount-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Received amount updated" />
        <div class="govuk-!-margin-top-9">
          <a class="govuk-link" routerLink="../.." [replaceUrl]="true"
            >Return to: Buy-out MoA {{ transaction().transactionCode }}</a
          >
        </div>
      </div>
    </div>
  `,
  imports: [RouterLink, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3ReceivedAmountConfirmationComponent {
  protected readonly transaction = signal(
    inject(ActivatedRoute).snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO,
  );
}

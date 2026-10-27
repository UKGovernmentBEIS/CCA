import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-cca3-change-status-confirmation',
  template: `
    <govuk-panel title="Payment status updated" />

    <div class="govuk-!-margin-top-9">
      <a class="govuk-link" [routerLink]="['../../']" [replaceUrl]="true" [relativeTo]="activatedRoute">
        Return to: Buy-out MoA {{ transactionCode }}</a
      >
    </div>
  `,
  imports: [RouterLink, PanelComponent],
})
export class Cca3ChangeStatusConfirmationComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly transactionCode = this.activatedRoute.snapshot.data.transactionDetails?.transactionCode;
}

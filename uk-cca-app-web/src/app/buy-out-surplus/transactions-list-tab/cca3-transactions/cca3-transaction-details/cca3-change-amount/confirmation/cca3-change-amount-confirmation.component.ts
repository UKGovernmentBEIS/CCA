import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { Cca3ChangeAmountState } from '../cca3-change-amount.state';

@Component({
  selector: 'cca-cca3-change-amount-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Current amount updated" />
        <div class="govuk-!-margin-top-9">
          <a class="govuk-link" [routerLink]="state.returnUrl()" [replaceUrl]="true">{{ state.returnText() }}</a>
        </div>
      </div>
    </div>
  `,
  imports: [RouterLink, PanelComponent],
})
export class Cca3ChangeAmountConfirmationComponent {
  protected readonly state = inject(Cca3ChangeAmountState);
}

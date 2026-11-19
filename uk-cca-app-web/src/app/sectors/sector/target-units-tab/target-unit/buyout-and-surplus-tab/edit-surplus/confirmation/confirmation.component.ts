import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>
          <strong>Surplus gained updated</strong>
        </govuk-panel>
        <a class="govuk-link" routerLink="../../../" [replaceUrl]="true" [fragment]="'buyout-surplus'">
          Return to: {{ targetUnitBusinessId }}
        </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly targetUnitBusinessId =
    this.activatedRoute.snapshot.data.targetUnit.targetUnitAccountDetails.businessId;
}

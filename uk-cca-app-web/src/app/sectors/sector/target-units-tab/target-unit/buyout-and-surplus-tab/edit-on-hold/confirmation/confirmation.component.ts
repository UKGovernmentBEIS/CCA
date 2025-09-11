import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <govuk-panel><strong>On hold status updated</strong></govuk-panel>
    <a class="govuk-link" routerLink="../../../" [replaceUrl]="true" [fragment]="'buyout-surplus'">
      Return to: {{ targetUnitBusinessId }}
    </a>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly targetUnitBusinessId =
    this.activatedRoute.snapshot.data.targetUnit.targetUnitAccountDetails.businessId;
}

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>
          <strong>{{ targetPeriod }} surplus gained updated</strong>
        </govuk-panel>
        <a class="govuk-link" routerLink="../../../../" [replaceUrl]="true" [fragment]="'buyout-surplus'">
          Return to: {{ targetUnitBusinessId }}
        </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly targetPeriod = this.activatedRoute.snapshot.paramMap.get('targetPeriodType');
  protected readonly targetUnitBusinessId =
    this.activatedRoute.snapshot.data.targetUnit.targetUnitAccountDetails.businessId;
}

import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>
          <strong>{{ targetPeriod() }} Buy-out cost per tCO2e updated</strong>
        </govuk-panel>

        <div class="govuk-!-margin-top-9">
          <a class="govuk-link" routerLink="/buyout-surplus" fragment="cost" [replaceUrl]="true">
            Return to: Buy-out and surplus
          </a>
        </div>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  private readonly route = inject(ActivatedRoute);

  protected readonly targetPeriod = signal(this.route.snapshot.paramMap.get('targetPeriodType'));
}

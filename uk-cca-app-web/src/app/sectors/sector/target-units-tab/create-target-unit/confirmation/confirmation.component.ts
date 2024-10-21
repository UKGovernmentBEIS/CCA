import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ButtonDirective, LinkDirective, PanelComponent } from '@netz/govuk-components';

import { CreateTargetUnitStore } from '../create-target-unit.store';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>Target unit {{ operatorName }} created</govuk-panel>

        <h3 class="govuk-heading-m">What happens next</h3>

        <p class="govuk-body">You can now apply for an underlying agreement.</p>

        <a govukLink [routerLink]="['/']" [replaceUrl]="true">Return to: your tasks</a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, ButtonDirective, LinkDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  readonly operatorName = inject(CreateTargetUnitStore)?.state?.name;
}

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

import { CreateTargetUnitStore } from '../create-target-unit.store';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>Target unit {{ operatorName }} created</govuk-panel>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p>You can now apply for an underlying agreement.</p>
        <a class="govuk-link" [routerLink]="['/']" [replaceUrl]="true">Return to: your tasks</a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  protected readonly operatorName = inject(CreateTargetUnitStore)?.state?.name;
}

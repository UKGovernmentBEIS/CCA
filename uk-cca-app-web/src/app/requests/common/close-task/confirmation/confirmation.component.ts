import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-non-compliance-conclusion-close-task-confirmation',
  template: `
    <govuk-panel title="Task closed successfully"></govuk-panel>

    <p class="govuk-body">You have marked this task as 'Closed'.</p>

    <h2 class="govuk-heading-m">What happens next</h2>
    <p class="govuk-body">You can start a new task if required.</p>

    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CloseTaskConfirmationComponent {}

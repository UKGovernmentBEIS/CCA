import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-provide-appeal-details-confirmation',
  template: `
    <govuk-panel title="Appeal registration details submitted"></govuk-panel>

    <h2 class="govuk-heading-m">What happens next</h2>
    <p class="govuk-body">A new task has been created to allow you to provide the appeal outcome.</p>
    <p class="govuk-body">
      Until you complete the appeal outcome task you will not be able to finalize the non-compliance conclusion.
    </p>

    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}

import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-appeal-outcome-confirmation',
  template: `
    <govuk-panel title="Appeal outcome completed" />

    <h2 class="govuk-heading-m">What happens next</h2>
    <p>A new task has been created to allow you to provide conclusion about the penalty notice.</p>

    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [PanelComponent, RouterLink],
})
export class ConfirmationComponent {}

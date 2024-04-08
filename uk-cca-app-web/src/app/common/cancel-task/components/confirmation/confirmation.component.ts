import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BaseSuccessComponent } from '@shared/base-success/base-success.component';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'cca-cancel-confirmation',
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Task cancelled"></govuk-panel>
      </div>
    </div>
    <a govukLink routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [GovukComponentsModule, RouterLink, AsyncPipe],
})
export class ConfirmationComponent extends BaseSuccessComponent {}

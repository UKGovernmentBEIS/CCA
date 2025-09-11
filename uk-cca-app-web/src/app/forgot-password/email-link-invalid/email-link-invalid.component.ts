import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-email-link-invalid',
  template: `
    <netz-page-heading>The password reset link has expired</netz-page-heading>
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <p>
          <a class="govuk-link" [routerLink]="['/forgot-password']"> Request another password reset email </a>
        </p>
      </div>
    </div>
  `,
  standalone: true,
  imports: [RouterModule, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailLinkInvalidComponent {}

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-page-not-found',
  template: `
    <netz-page-heading size="xl">Page Not Found</netz-page-heading>
    <p class="govuk-body">If you typed the web address, check it is correct.</p>
    <p class="govuk-body">If you pasted the web address, check you copied the entire address.</p>
    <p class="govuk-body">
      If the web address is correct,
      <a class="govuk-link" [routerLink]="['/contact-us']"> contact your regulator </a>
      for help.
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent, RouterModule],
})
export class PageNotFoundComponent {}

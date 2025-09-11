import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-contact-us',
  template: `
    <netz-page-heading size="xl">Contact the CCA helpdesk</netz-page-heading>
    <p>If you need help accessing or using the CCA service, please contact your regulator.</p>
    <ul class="govuk-list govuk-list--bullet" style="list-style-type: none">
      <li>cca-help&#64;environment-agency.gov.uk</li>
    </ul>
  `,
  standalone: true,
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactUsComponent {}

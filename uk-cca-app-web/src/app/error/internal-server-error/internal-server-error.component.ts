import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-internal-server-error',
  template: `
    <netz-page-heading size="xl">Sorry, there is a problem with the service</netz-page-heading>
    <p>Try again later.</p>
    <p>
      <a href="mailto:cca@environment-agency.gov.uk" class="govuk-link govuk-!-font-weight-bold">
        Contact the DESNZ helpdesk</a
      >
      if you have any questions.
    </p>
  `,
  standalone: true,
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InternalServerErrorComponent {}

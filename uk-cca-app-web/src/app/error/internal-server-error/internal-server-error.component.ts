import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-internal-server-error',
  template: `
    <cca-page-heading size="xl">Sorry, there is a problem with the service</cca-page-heading>

    <p class="govuk-body">Try again later.</p>

    <p class="govuk-body">
      <a href="mailto:cca@environment-agency.gov.uk" govukLink class="govuk-!-font-weight-bold">
        Contact the DESNZ helpdesk</a
      >
      if you have any questions.
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent],
})
export class InternalServerErrorComponent {}

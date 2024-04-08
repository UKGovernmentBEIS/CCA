import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { VERSION } from '../../environments/version';

@Component({
  selector: 'cca-version',
  standalone: true,
  template: `
    <cca-page-heading caption="Information about the application version" size="l">About</cca-page-heading>
    <p class="govuk-body">
      Version: <span class="govuk-!-font-weight-bold">{{ version.devVersion }}</span>
    </p>
    <p class="govuk-body">
      Commit hash: <span class="govuk-!-font-weight-bold">{{ version.hash }}</span>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent],
})
export class VersionComponent {
  version = VERSION;
}

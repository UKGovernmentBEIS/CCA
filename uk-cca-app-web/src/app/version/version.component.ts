import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

import { VERSION } from '../../environments/version';

@Component({
  selector: 'cca-version',
  template: `
    <netz-page-heading caption="Information about the application version" size="l">About</netz-page-heading>
    <p>Version: <span class="govuk-!-font-weight-bold">RELEASE_VERSION</span></p>
    <p>
      Commit hash: <span class="govuk-!-font-weight-bold">{{ version.hash }}</span>
    </p>
  `,
  standalone: true,
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VersionComponent {
  version = VERSION;
}

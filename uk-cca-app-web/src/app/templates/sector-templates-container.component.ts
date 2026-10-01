import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SectorTemplatesComponent } from '@shared/components';

@Component({
  template: `
    <h1 class="govuk-heading-xl">Templates</h1>
    <cca-sector-templates />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SectorTemplatesComponent],
})
export class SectorTemplatesContainerComponent {}

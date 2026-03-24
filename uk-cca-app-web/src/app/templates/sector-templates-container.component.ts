import { Component } from '@angular/core';

import { SectorTemplatesComponent } from '@shared/components';

@Component({
  template: `
    <h1 class="govuk-heading-xl">Templates</h1>
    <cca-sector-templates />
  `,
  imports: [SectorTemplatesComponent],
})
export class SectorTemplatesContainerComponent {}

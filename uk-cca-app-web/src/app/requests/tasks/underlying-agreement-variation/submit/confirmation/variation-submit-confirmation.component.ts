import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ConfirmationSharedComponent } from '@shared/components';

@Component({
  selector: 'cca-una-variation-submit-confirmation',
  template: `
    <cca-confirmation-shared
      title="Variation application sent to regulator"
      [whatHappensNextTemplate]="whatHappensNextTemplate"
    />

    <ng-template #whatHappensNextTemplate>
      <h3 class="govuk-heading-m">What happens next</h3>
      <p>The regulator will review your variation application and contact you.</p>
    </ng-template>
  `,
  imports: [ConfirmationSharedComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationSubmitConfirmationComponent {}
